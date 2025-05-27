package com.github.starter.app.epl.endpoint

import com.github.starter.app.epl.model.Team
import com.github.starter.core.container.Container
import com.github.starter.modules.epl.endpoint.EplEndpoint
import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import com.github.starter.modules.epl.repository.MatchRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class EplEndpointTest {
    @Mock
    private lateinit var matchRepository: MatchRepository
    private lateinit var eplEndpoint: EplEndpoint

    @BeforeEach

    fun setUp() {
        eplEndpoint = EplEndpoint(matchRepository)
    }

    @Test

    fun `biggestMargin should return flux of EplMatch`() {
        val season = 2022
        val matches = Flux.just(
            EplMatch(
                season = 2022,
                wk = 1,
                matchDate = LocalDate.now(),
                team = "Team A",
                opponent = "Team B",
                venue = "Stadium A",
                result = "W",
                gf = 5,
                ga = 0,
                points = 3
            )
        )
        `when`(matchRepository.biggestMargin(season)).thenReturn(matches)
        val result = eplEndpoint.biggestMargin(season)
        assertNotNull(result)
        assertEquals(matches, result)
    }

    @Test

    fun `mostGoals should return flux of EplMatch`() {
        val season = 2022
        val matches = Flux.just(
            EplMatch(
                season = 2022,
                wk = 1,
                matchDate = LocalDate.now(),
                team = "Team A",
                opponent = "Team B",
                venue = "Stadium A",
                result = "W",
                gf = 5,
                ga = 4,
                points = 3
            )
        )
        `when`(matchRepository.mostGoalsScored(season)).thenReturn(matches)
        val result = eplEndpoint.mostGoals(season)
        assertNotNull(result)
        assertEquals(matches, result)
    }

    @Test

    fun `seasonPerformance should return flux of EplStanding`() {
        val team = "Team A"
        val season = 2022
        val standings = Flux.just(
            EplStanding(
                season = 2022,
                ranking = 1,
                team = "Team A",
                played = 10,
                gf = 17,
                ga = 10,
                gd = 7,
                points = 17
            )
        )
        `when`(matchRepository.seasonPerformance(team, season)).thenReturn(standings)
        val result = eplEndpoint.seasonPerformance(team, season)
        assertNotNull(result)
        assertEquals(standings, result)
    }

    @Test

    fun `seasonTable should return flux of EplStanding`() {
        val season = 2022
        val standings = Flux.just(
            EplStanding(
                season = 2022,
                ranking = 1,
                team = "Team A",
                played = 10,
                gf = 17,
                ga = 10,
                gd = 7,
                points = 17
            )
        )
        `when`(matchRepository.seasonTable(season)).thenReturn(standings)
        val result = eplEndpoint.seasonTable(season)
        assertNotNull(result)
        assertEquals(standings, result)
    }

    @Test

    fun `winner should return flux of EplStanding`() {
        val season = 2022
        val standings = Flux.just(
            EplStanding(
                season = 2022,
                ranking = 1,
                team = "Team A",
                played = 10,
                gf = 17,
                ga = 10,
                gd = 7,
                points = 17
            )
        )
        `when`(matchRepository.winner(season)).thenReturn(standings)
        val result = eplEndpoint.winner(season)
        assertNotNull(result)
        assertEquals(standings, result)
    }

    @Test

    fun `allTeams should return mono of Container of Team`() {
        val season = 2022
        val pageStateToken = ""
        val teams = listOf(Team("Team A"))
        `when`(matchRepository.allTeams(season, 0, 10)).thenReturn(Flux.fromIterable(teams))
        val result = eplEndpoint.allTeams(season, pageStateToken)
        val containerResult = result.block()
        assertNotNull(containerResult)
        assertEquals(teams, containerResult?.data)
        assertEquals(emptyMap<String, Any>(), containerResult?.metadata)
    }

    @Test

    fun `allTeams with pageStateToken should return mono of Container of Team`() {
        val season = 2022
        val pageStateToken = "MTA6MTA="
        val teams = List(10) { Team("Team B $it") }
        `when`(matchRepository.allTeams(season, 10, 10)).thenReturn(Flux.fromIterable(teams))
        val result = eplEndpoint.allTeams(season, pageStateToken)
        val containerResult = result.block()
        assertNotNull(containerResult)
        assertEquals(teams, containerResult?.data)
        assertNotNull(containerResult?.metadata?.get("nextToken"))
        assertEquals(1, containerResult?.metadata?.size)
    }
}
