package com.github.starter.app.pokemon.repository

import com.github.pokemon.model.Pokemon // Actual import from the repository
import com.github.starter.app.config.JdbcClientFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class EntriesRepositoryTest {

    @Mock
    private lateinit var jdbcClientFactory: JdbcClientFactory

    @Mock
    private lateinit var configuredJdbcClient: com.github.starter.app.config.JdbcClient

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) // For fluent API
    private lateinit var databaseClient: DatabaseClient

    // Mocks for the fluent API calls from DatabaseClient
    @Mock
    private lateinit var genericExecuteSpec: DatabaseClient.GenericExecuteSpec

    @Mock
    private lateinit var rowsFetchSpecPokemon: RowsFetchSpec<Pokemon>

    private lateinit var entriesRepository: EntriesRepository

    @Captor
    private lateinit var sqlCaptor: ArgumentCaptor<String>

    // Helper to create mocked Pokemon instances
    private fun createMockPokemon(name: String): Pokemon {
        val mockPokemon = mock<Pokemon>()
        whenever(mockPokemon.toString()).thenReturn("Pokemon(name=$name)") // For simple list comparison
        return mockPokemon
    }

    @BeforeEach
    fun setUp() {
        whenever(jdbcClientFactory.forName(any())).thenReturn(configuredJdbcClient)
        whenever(configuredJdbcClient.client()).thenReturn(databaseClient)
        
        // Assuming mapProperties is a method on GenericExecuteSpec returning RowsFetchSpec<Pokemon>
        // This might need adjustment if mapProperties is an extension function or has a different signature.
        whenever(databaseClient.sql(sqlCaptor.capture())).thenReturn(genericExecuteSpec)
        whenever(genericExecuteSpec.mapProperties(eq(Pokemon::class.java))).thenReturn(rowsFetchSpecPokemon)

        entriesRepository = EntriesRepository(jdbcClientFactory, "testClient")
    }

    @Test
    fun `listEntries with default parameters should query correctly and return pokemon flux`() {
        val pokemon1 = createMockPokemon("Pikachu")
        val pokemon2 = createMockPokemon("Charmander")
        val expectedPokemonList = listOf(pokemon1, pokemon2)
        whenever(rowsFetchSpecPokemon.all()).thenReturn(Flux.fromIterable(expectedPokemonList))

        val resultFlux = entriesRepository.listEntries() // Call with default params

        StepVerifier.create(resultFlux)
            .expectNext(pokemon1)
            .expectNext(pokemon2)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assertTrue(capturedSql.contains("select * from app.pokemons", ignoreCase = true))
        assertTrue(capturedSql.contains("limit 10", ignoreCase = true)) // Default limit
        assertTrue(capturedSql.contains("offset 0", ignoreCase = true)) // Default offset
        verify(genericExecuteSpec).mapProperties(Pokemon::class.java)
    }

    @Test
    fun `listEntries with custom parameters should query correctly and return pokemon flux`() {
        val customStart = 5
        val customLimit = 2
        val pokemon1 = createMockPokemon("Bulbasaur")
        val expectedPokemonList = listOf(pokemon1)
        whenever(rowsFetchSpecPokemon.all()).thenReturn(Flux.fromIterable(expectedPokemonList))

        val resultFlux = entriesRepository.listEntries(start = customStart, limit = customLimit)

        StepVerifier.create(resultFlux)
            .expectNext(pokemon1)
            .verifyComplete()

        verify(databaseClient).sql(sqlCaptor.capture())
        val capturedSql = sqlCaptor.value
        assertTrue(capturedSql.contains("select * from app.pokemons", ignoreCase = true))
        assertTrue(capturedSql.contains("limit $customLimit", ignoreCase = true))
        assertTrue(capturedSql.contains("offset $customStart", ignoreCase = true))
        verify(genericExecuteSpec).mapProperties(Pokemon::class.java)
    }

    @Test
    fun `listEntries with empty result from database should return empty flux`() {
        whenever(rowsFetchSpecPokemon.all()).thenReturn(Flux.empty())

        val resultFlux = entriesRepository.listEntries()

        StepVerifier.create(resultFlux)
            .verifyComplete() // Expect no items

        verify(databaseClient).sql(sqlCaptor.capture()) // Ensure SQL was still called
        verify(genericExecuteSpec).mapProperties(Pokemon::class.java)
    }
}
