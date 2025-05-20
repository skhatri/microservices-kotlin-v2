package com.github.starter.app.epl.repository

import com.github.starter.app.config.JdbcClientFactory
import com.github.starter.app.epl.model.Team
import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import com.github.starter.modules.epl.repository.Mappers
import com.github.starter.modules.epl.repository.PgMatchRepository
import io.r2dbc.spi.Readable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.* // Import all Mockito-Kotlin extensions
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.LocalDate
import java.util.function.Function

@ExtendWith(MockitoExtension::class)
class PgMatchRepositoryTest {

    @Mock
    private lateinit var jdbcClientFactory: JdbcClientFactory

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) // For fluent API: client.sql("...").bind(...).map(...).all()
    private lateinit var databaseClient: DatabaseClient

    @Mock
    private lateinit var configuredJdbcClient: com.github.starter.app.config.JdbcClient // Mock for the intermediate client object

    // Mocks for the fluent API calls
    @Mock
    private lateinit var genericExecuteSpec: DatabaseClient.GenericExecuteSpec

    @Mock
    private lateinit var rowsFetchSpecEplMatch: RowsFetchSpec<EplMatch>

    @Mock
    private lateinit var rowsFetchSpecEplStanding: RowsFetchSpec<EplStanding>

    @Mock
    private lateinit var rowsFetchSpecString: RowsFetchSpec<String>


    private lateinit var pgMatchRepository: PgMatchRepository

    @Captor
    private lateinit var sqlCaptor: ArgumentCaptor<String>

    @BeforeEach
    fun setUp() {
        whenever(jdbcClientFactory.forName(any())).thenReturn(configuredJdbcClient) // factory.forName returns ConfiguredJdbcClient
        whenever(configuredJdbcClient.client()).thenReturn(databaseClient) // configuredJdbcClient.client() returns DatabaseClient
        pgMatchRepository = PgMatchRepository(jdbcClientFactory, "testClient")
    }

    private fun mockDatabaseClientSqlSetup() { // Renamed to avoid confusion, only sets up the initial sql call
        whenever(databaseClient.sql(sqlCaptor.capture())).thenReturn(genericExecuteSpec)
    }

    @Test
    fun `biggestMargin should query and map results`() {
        mockDatabaseClientSqlSetup()
        val expectedMatch = EplMatch(season = 2022, team = "Arsenal", gf = 5, ga = 0)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplMatch>>())).thenReturn(rowsFetchSpecEplMatch)
        whenever(rowsFetchSpecEplMatch.all()).thenReturn(Flux.just(expectedMatch))

        val result = pgMatchRepository.biggestMargin(2022)

        StepVerifier.create(result)
            .expectNext(expectedMatch)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("FROM app.epl_team_match"))
        assert(capturedSql.contains("season = 2022"))
        assert(capturedSql.contains("abs(gf-ga) = ("))
    }
    
    @Test
    fun `biggestMargin with season -1 should query all seasons`() {
        mockDatabaseClientSqlSetup()
        val expectedMatch = EplMatch(season = 2022, team = "Arsenal", gf = 5, ga = 0)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplMatch>>())).thenReturn(rowsFetchSpecEplMatch)
        whenever(rowsFetchSpecEplMatch.all()).thenReturn(Flux.just(expectedMatch))

        pgMatchRepository.biggestMargin(-1).blockLast() // Use blockLast to ensure execution

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("season <> -1"))
    }

    @Test
    fun `mostGoalsScored should query and map results`() {
        mockDatabaseClientSqlSetup()
        val expectedMatch = EplMatch(season = 2022, team = "Man City", gf = 6)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplMatch>>())).thenReturn(rowsFetchSpecEplMatch)
        whenever(rowsFetchSpecEplMatch.all()).thenReturn(Flux.just(expectedMatch))

        val result = pgMatchRepository.mostGoalsScored(2022)

        StepVerifier.create(result)
            .expectNext(expectedMatch)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("FROM app.epl_team_match"))
        assert(capturedSql.contains("season = 2022"))
        assert(capturedSql.contains("gf = ("))
    }
    
    @Test
    fun `mostGoalsScored with season -1 should query all seasons`() {
        mockDatabaseClientSqlSetup()
        val expectedMatch = EplMatch(season = 2022, team = "Man City", gf = 6)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplMatch>>())).thenReturn(rowsFetchSpecEplMatch)
        whenever(rowsFetchSpecEplMatch.all()).thenReturn(Flux.just(expectedMatch))

        pgMatchRepository.mostGoalsScored(-1).blockLast()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("season <> -1"))
    }

    @Test
    fun `seasonPerformance should query with team and season and map results`() {
        mockDatabaseClientSqlSetup()
        val team = "Arsenal"
        val season = 2022
        val expectedStanding = EplStanding(season = season, team = team, ranking = 1)

        // Specific stubbing for bind call in this method
        whenever(genericExecuteSpec.bind(eq("$1"), eq(team))).thenReturn(genericExecuteSpec)

        whenever(genericExecuteSpec.map(any<Function<Readable, EplStanding>>())).thenReturn(rowsFetchSpecEplStanding)
        whenever(rowsFetchSpecEplStanding.all()).thenReturn(Flux.just(expectedStanding))
        
        val result = pgMatchRepository.seasonPerformance(team, season)

        StepVerifier.create(result)
            .expectNext(expectedStanding)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("FROM app.epl_standings"))
        assert(capturedSql.contains("season = 2022"))
        assert(capturedSql.contains("AND team = $1"))
        verify(genericExecuteSpec).bind(eq("$1"), eq(team))
    }

    @Test
    fun `seasonTable should query with season and map results`() {
        mockDatabaseClientSqlSetup()
        val season = 2022
        val expectedStanding = EplStanding(season = season, team = "Arsenal", ranking = 1)

        // Specific stubbing for bind call in this method
        whenever(genericExecuteSpec.bind(eq("$1"), eq(season))).thenReturn(genericExecuteSpec)

        whenever(genericExecuteSpec.map(any<Function<Readable, EplStanding>>())).thenReturn(rowsFetchSpecEplStanding)
        whenever(rowsFetchSpecEplStanding.all()).thenReturn(Flux.just(expectedStanding))

        val result = pgMatchRepository.seasonTable(season)

        StepVerifier.create(result)
            .expectNext(expectedStanding)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("FROM app.epl_standings"))
        assert(capturedSql.contains("WHERE season = $1"))
        assert(capturedSql.contains("ORDER BY ranking ASC"))
        verify(genericExecuteSpec).bind(eq("$1"), eq(season))
    }

    @Test
    fun `winner should query with season and map results`() {
        mockDatabaseClientSqlSetup()
        val season = 2022
        val expectedStanding = EplStanding(season = season, team = "Arsenal", ranking = 1)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplStanding>>())).thenReturn(rowsFetchSpecEplStanding)
        whenever(rowsFetchSpecEplStanding.all()).thenReturn(Flux.just(expectedStanding))

        val result = pgMatchRepository.winner(season)

        StepVerifier.create(result)
            .expectNext(expectedStanding)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("FROM app.epl_standings"))
        assert(capturedSql.contains("season = 2022"))
        assert(capturedSql.contains("AND ranking = 1"))
    }
    
    @Test
    fun `winner with season -1 should query all seasons`() {
        mockDatabaseClientSqlSetup()
        val expectedStanding = EplStanding(season = 2022, team = "Arsenal", ranking = 1)
        whenever(genericExecuteSpec.map(any<Function<Readable, EplStanding>>())).thenReturn(rowsFetchSpecEplStanding)
        whenever(rowsFetchSpecEplStanding.all()).thenReturn(Flux.just(expectedStanding))

        pgMatchRepository.winner(-1).blockLast()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("season <> -1"))
    }

    @Test
    fun `allTeams should query with season, limit, offset and map results`() {
        mockDatabaseClientSqlSetup()
        val season = 2022
        val start = 0
        val limit = 10
        val expectedTeamName = "Arsenal"
        val expectedTeam = Team(expectedTeamName)
        
        // Mocking the chain for allTeams specifically
        // databaseClient.sql -> genericExecuteSpec
        // genericExecuteSpec.map(Mappers.field(...)) -> rowsFetchSpecString
        // rowsFetchSpecString.all() -> Flux<String>
        // then the final .map { Team(it) } is done by reactor on the Flux<String>
        
        whenever(genericExecuteSpec.map(any<Function<Readable, String>>())).thenReturn(rowsFetchSpecString)
        whenever(rowsFetchSpecString.all()).thenReturn(Flux.just(expectedTeamName))

        val result = pgMatchRepository.allTeams(season, start, limit)

        StepVerifier.create(result)
            .expectNext(expectedTeam)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assert(capturedSql.contains("SELECT DISTINCT team"))
        assert(capturedSql.contains("FROM app.epl_standings"))
        assert(capturedSql.contains("season = 2022"))
        assert(capturedSql.contains("LIMIT $limit"))
        assert(capturedSql.contains("OFFSET $start"))
    }
}
