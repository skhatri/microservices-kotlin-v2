package com.github.starter.app.pokemon.endpoint

import com.github.pokemon.model.Pokemon
import com.github.starter.app.pokemon.repository.EntriesRepository
import com.github.starter.core.container.Container
import com.github.starter.core.container.TokenInformation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.Base64

@ExtendWith(MockitoExtension::class)
class EntriesEndpointTest {
    @Mock
    private lateinit var entriesRepository: EntriesRepository
    private lateinit var entriesEndpoint: EntriesEndpoint

    @BeforeEach

    fun setUp() {
        entriesEndpoint = EntriesEndpoint(entriesRepository)
    }

    private fun createPokemon(id: Int, name: String): Pokemon {
        val mockPokemon = org.mockito.Mockito.mock(Pokemon::class.java)
        whenever(mockPokemon.toString()).thenReturn("Pokemon(name=$name)")
        return mockPokemon
    }

    @Test

    fun `listEntries with no pageStateToken should use default pagination and return items`() {
        val defaultStart = 0
        val defaultLimit = 10
        val pokemonList = listOf(createPokemon(1, "Pikachu"), createPokemon(2, "Charmander"))
        val expectedPokemonFlux = Flux.fromIterable(pokemonList)
        whenever(entriesRepository.listEntries(eq(defaultStart), eq(defaultLimit))).thenReturn(expectedPokemonFlux)
        val resultMono = entriesEndpoint.listEntries("")
        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertEquals(pokemonList.size, container.data?.size)
                assertEquals(pokemonList.toString(), container.data.toString())
                assertEquals(emptyMap<String, Any>(), container.metadata)
            }
            .verifyComplete()
    }

    @Test

    fun `listEntries with pageStateToken should use decoded pagination and return items`() {
        val tokenStart = 10
        val tokenLimit = 5
        val pageStateToken = Base64.getEncoder().encodeToString("$tokenStart:$tokenLimit".toByteArray())
        val pokemonList = listOf(createPokemon(11, "Bulbasaur"))
        val expectedPokemonFlux = Flux.fromIterable(pokemonList)
        whenever(entriesRepository.listEntries(eq(tokenStart), eq(tokenLimit))).thenReturn(expectedPokemonFlux)
        val resultMono = entriesEndpoint.listEntries(pageStateToken)
        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertEquals(pokemonList.size, container.data?.size)
                assertEquals(pokemonList.toString(), container.data.toString())
                assertEquals(emptyMap<String, Any>(), container.metadata)
            }
            .verifyComplete()
    }

    @Test

    fun `listEntries with pageStateToken resulting in full page should generate nextToken`() {
        val tokenStart = 0
        val tokenLimit = 2
        val pageStateToken = Base64.getEncoder().encodeToString("$tokenStart:$tokenLimit".toByteArray())
        val pokemonList = listOf(createPokemon(1, "Pikachu"), createPokemon(2, "Charmander"))
        val expectedPokemonFlux = Flux.fromIterable(pokemonList)
        whenever(entriesRepository.listEntries(eq(tokenStart), eq(tokenLimit))).thenReturn(expectedPokemonFlux)
        val resultMono = entriesEndpoint.listEntries(pageStateToken)
        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertEquals(pokemonList.size, container.data?.size)
                assertEquals(pokemonList.toString(), container.data.toString())
                assertNotNull(container.metadata["nextToken"])
                assertEquals(1, container.metadata.size)
                val expectedNextToken = Base64.getEncoder().encodeToString("4:2".toByteArray())
                assertEquals(expectedNextToken, container.metadata["nextToken"])
            }
            .verifyComplete()
    }

    @Test

    fun `listEntries with empty repository result should return empty container`() {
        val defaultStart = 0
        val defaultLimit = 10
        whenever(entriesRepository.listEntries(eq(defaultStart), eq(defaultLimit))).thenReturn(Flux.empty())
        val resultMono = entriesEndpoint.listEntries("")
        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertTrue(container.data?.isEmpty() ?: false)
                assertEquals(emptyMap<String, Any>(), container.metadata)
            }
            .verifyComplete()
    }
}
