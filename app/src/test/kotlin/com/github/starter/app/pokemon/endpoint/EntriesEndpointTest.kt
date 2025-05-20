package com.github.starter.app.pokemon.endpoint

import com.github.pokemon.model.Pokemon // Actual import from the endpoint
import com.github.starter.app.pokemon.repository.EntriesRepository
import com.github.starter.core.container.Container
import com.github.starter.core.container.TokenInformation
import org.junit.jupiter.api.Assertions.* // Wildcard import for all assertions
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

// If com.github.pokemon.model.Pokemon is not available during test compilation,
// this dummy class can be used. However, it's better if the actual class is available.
// For now, assuming the actual class will be resolved from dependencies.
// data class Pokemon(val id: Int, val name: String) 

@ExtendWith(MockitoExtension::class)
class EntriesEndpointTest {

    @Mock
    private lateinit var entriesRepository: EntriesRepository

    private lateinit var entriesEndpoint: EntriesEndpoint

    @BeforeEach
    fun setUp() {
        entriesEndpoint = EntriesEndpoint(entriesRepository)
    }

    // Helper to create Pokemon instances, assuming a constructor or builder.
    // This will need to match the actual Pokemon class structure.
    // For now, let's assume it has at least a name, and we'll mock its toString for simplicity in some assertions.
    private fun createPokemon(id: Int, name: String): Pokemon {
        // This is a placeholder. The actual Pokemon class might be complex.
        // We might need to use a mocking framework for Pokemon itself if it's an interface
        // or use reflection if we don't know its constructor.
        // For now, let's assume it's a simple data class like: data class Pokemon(val id: Int, val name: String)
        // If the actual Pokemon.kt is not part of the source for `read_files`, this is a guess.
        // Let's try to proceed as if it's a simple data class.
        // The import `com.github.pokemon.model.Pokemon` suggests it's defined elsewhere.
        // We'll mock its properties if direct instantiation is problematic.
        val mockPokemon = org.mockito.Mockito.mock(Pokemon::class.java)
        whenever(mockPokemon.toString()).thenReturn("Pokemon(name=$name)") // Example for toString
        // whenever(mockPokemon.name).thenReturn(name) // If it has a name property
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
                assertEquals(pokemonList.toString(), container.data.toString()) // Comparing string representations for now
                
                // Metadata check: items.size (2) < limit (10), so metadata should be empty
                assertEquals(emptyMap<String, Any>(), container.metadata)
            }
            .verifyComplete()
    }

    @Test
    fun `listEntries with pageStateToken should use decoded pagination and return items`() {
        val tokenStart = 10
        val tokenLimit = 5
        val pageStateToken = Base64.getEncoder().encodeToString("$tokenStart:$tokenLimit".toByteArray()) // "10:5"
        
        val pokemonList = listOf(createPokemon(11, "Bulbasaur"))
        val expectedPokemonFlux = Flux.fromIterable(pokemonList)

        whenever(entriesRepository.listEntries(eq(tokenStart), eq(tokenLimit))).thenReturn(expectedPokemonFlux)

        val resultMono = entriesEndpoint.listEntries(pageStateToken)

        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertEquals(pokemonList.size, container.data?.size)
                assertEquals(pokemonList.toString(), container.data.toString())

                // Metadata check: items.size (1) < limit (5), so metadata should be empty
                assertEquals(emptyMap<String, Any>(), container.metadata)
            }
            .verifyComplete()
    }

    @Test
    fun `listEntries with pageStateToken resulting in full page should generate nextToken`() {
        val tokenStart = 0
        val tokenLimit = 2 // Small limit for test
        val pageStateToken = Base64.getEncoder().encodeToString("$tokenStart:$tokenLimit".toByteArray()) // "0:2"
        
        val pokemonList = listOf(createPokemon(1, "Pikachu"), createPokemon(2, "Charmander")) // items.size (2) == limit (2)
        val expectedPokemonFlux = Flux.fromIterable(pokemonList)

        whenever(entriesRepository.listEntries(eq(tokenStart), eq(tokenLimit))).thenReturn(expectedPokemonFlux)

        val resultMono = entriesEndpoint.listEntries(pageStateToken)

        StepVerifier.create(resultMono)
            .assertNext { container ->
                assertNotNull(container)
                assertEquals(pokemonList.size, container.data?.size)
                assertEquals(pokemonList.toString(), container.data.toString())

                // Metadata check: items.size (2) == limit (2), so nextToken should exist
                assertNotNull(container.metadata["nextToken"])
                assertEquals(1, container.metadata.size) // Only nextToken
                
                // Verify nextToken content: TokenInformation.encode(currentStart + currentLimit, currentLimit)
                // currentStart = 0, currentLimit = 2. So, encode(0 + 2, 2)
                // TokenInformation.encode(2, 2) produces Base64 of "(2+2):2" = "4:2"
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
                assertEquals(emptyMap<String, Any>(), container.metadata) // No items, no nextToken
            }
            .verifyComplete()
    }
}
