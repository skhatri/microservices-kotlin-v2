package com.github.pokemon.model // Package matches the class being tested

import org.junit.jupiter.api.Test // Keep for @Test annotation
import java.math.BigDecimal
import kotlin.test.* // For assertions like assertEquals, assertNull, assertTrue, assertFailsWith, assertContentEquals

class PokemonTest {

    private fun createPokemonInstance(
        name: String = "Pikachu",
        baseStat: Int = 320,
        primaryType: String = "Electric",
        secondaryType: String? = null,
        location: String = "Viridian Forest",
        legendary: Boolean? = false,
        weakness: Array<String> = arrayOf("Ground"),
        height: BigDecimal = BigDecimal("0.4"),
        weight: BigDecimal = BigDecimal("6.0")
    ): Pokemon {
        return Pokemon(name, baseStat, primaryType, secondaryType, location, legendary, weakness, height, weight)
    }

    @Test
    fun `Pokemon can be instantiated with all properties`() {
        val pokemon = createPokemonInstance(
            secondaryType = "Steel",
            legendary = true,
            weakness = arrayOf("Ground", "Fire")
        )
        assertEquals("Pikachu", pokemon.name)
        assertEquals(320, pokemon.baseStat)
        assertEquals("Electric", pokemon.primaryType)
        assertEquals("Steel", pokemon.secondaryType)
        assertEquals("Viridian Forest", pokemon.location)
        assertTrue(pokemon.legendary ?: false)
        assertContentEquals(arrayOf("Ground", "Fire"), pokemon.weakness)
        assertEquals(BigDecimal("0.4"), pokemon.height)
        assertEquals(BigDecimal("6.0"), pokemon.weight)
    }

    @Test
    fun `equals and hashCode adhere to custom implementation`() {
        val p1 = createPokemonInstance()
        val p2 = createPokemonInstance() // Identical to p1
        val p3 = createPokemonInstance(name = "Charmander")
        val p4 = createPokemonInstance(weakness = arrayOf("Water"))
        val p5 = createPokemonInstance(secondaryType = "Flying")
        val p6 = createPokemonInstance(legendary = true)
        val p7 = createPokemonInstance(legendary = null)


        assertEquals(p1, p2)
        assertEquals(p1.hashCode(), p2.hashCode())

        assertNotEquals(p1, p3)
        assertNotEquals(p1, p4) // Different weakness array content
        assertNotEquals(p1, p5) // p1 secondaryType is null, p5 is "Flying"
        assertNotEquals(p1, p6) // p1 legendary is false, p6 is true
        assertNotEquals(p6, p7) // p6 legendary is true, p7 is null

        // Test array content equals specifically
        val p1_weak_clone = createPokemonInstance(weakness = arrayOf("Ground"))
        assertEquals(p1, p1_weak_clone)
        assertEquals(p1.hashCode(), p1_weak_clone.hashCode())
    }
    
    @Test
    fun `equals handles nullability for secondaryType and legendary`() {
        val base = createPokemonInstance(secondaryType = null, legendary = null)
        val withSecondary = createPokemonInstance(secondaryType = "Flying", legendary = null)
        val withLegendary = createPokemonInstance(secondaryType = null, legendary = true)

        assertNotEquals(base, withSecondary)
        assertNotEquals(base, withLegendary)
        assertNotEquals(withSecondary, withLegendary)
    }


    @Test
    fun `toString provides a non-empty representation`() {
        val pokemon = createPokemonInstance()
        assertTrue(pokemon.toString().startsWith("Pokemon(")) // Default data class toString format
        assertTrue(pokemon.toString().contains("name=Pikachu"))
    }

    @Test
    fun `copy works as expected`() {
        val p1 = createPokemonInstance()
        val p2 = p1.copy(name = "Raichu", baseStat = 485)

        assertEquals("Pikachu", p1.name) // Original unchanged
        assertEquals("Raichu", p2.name)
        assertEquals(485, p2.baseStat)
        assertEquals(p1.primaryType, p2.primaryType) // Unchanged property

        val p3 = p1.copy()
        assertEquals(p1, p3)
        assertNotSame(p1, p3)
    }

    @Test
    fun `fromMap creates Pokemon from valid map`() {
        val map = mapOf<String, Any?>(
            "name" to "Bulbasaur",
            "base_stat" to 318,
            "primary_type" to "Grass",
            "secondary_type" to "Poison",
            "location" to "Starter",
            "legendary" to false,
            "weakness" to arrayOf("Fire", "Psychic", "Flying", "Ice"),
            "height" to BigDecimal("0.7"),
            "weight" to BigDecimal("6.9")
        )
        val pokemon = Pokemon.fromMap(map) // assertDoesNotThrow is from JUnit, can be removed if not specifically testing throw/no-throw

        assertEquals("Bulbasaur", pokemon.name)
        assertEquals(318, pokemon.baseStat)
        assertEquals("Grass", pokemon.primaryType)
        assertEquals("Poison", pokemon.secondaryType)
        assertEquals("Starter", pokemon.location)
        assertEquals(false, pokemon.legendary)
        assertContentEquals(arrayOf("Fire", "Psychic", "Flying", "Ice"), pokemon.weakness)
        assertEquals(BigDecimal("0.7"), pokemon.height)
        assertEquals(BigDecimal("6.9"), pokemon.weight)
    }

    @Test
    fun `fromMap handles nullable fields when absent from map`() {
        // secondary_type and legendary are nullable
        val map = mapOf<String, Any?>(
            "name" to "Squirtle",
            "base_stat" to 314,
            "primary_type" to "Water",
            // "secondary_type" is missing
            "location" to "Starter",
            // "legendary" is missing
            "weakness" to arrayOf("Grass", "Electric"),
            "height" to BigDecimal("0.5"),
            "weight" to BigDecimal("9.0")
        )
        val pokemon = Pokemon.fromMap(map)

        assertEquals("Squirtle", pokemon.name)
        assertNull(pokemon.secondaryType)
        assertNull(pokemon.legendary)
    }
    
    @Test
    fun `fromMap handles nullable fields when explicitly null in map`() {
        val map = mapOf<String, Any?>(
            "name" to "Charmander",
            "base_stat" to 309,
            "primary_type" to "Fire",
            "secondary_type" to null, // Explicitly null
            "location" to "Starter",
            "legendary" to null,      // Explicitly null
            "weakness" to arrayOf("Water", "Ground", "Rock"),
            "height" to BigDecimal("0.6"),
            "weight" to BigDecimal("8.5")
        )
        val pokemon = Pokemon.fromMap(map)

        assertEquals("Charmander", pokemon.name)
        assertNull(pokemon.secondaryType)
        assertNull(pokemon.legendary)
    }

    @Test
    fun `fromMap throws NPE if non-nullable field 'name' is missing`() {
        val map = mapOf<String, Any?>( // 'name' is missing
            "base_stat" to 318,
            "primary_type" to "Grass",
            "location" to "Starter",
            "weakness" to arrayOf("Fire"),
            "height" to BigDecimal("0.7"),
            "weight" to BigDecimal("6.9")
        )
        assertFailsWith<NullPointerException> {
            Pokemon.fromMap(map)
        }
    }
    
    @Test
    fun `fromMap throws ClassCastException for incorrect type in map for Int`() {
        val map = mapOf<String, Any?>(
            "name" to "TypeErr",
            "base_stat" to "should_be_int", // Incorrect type
            "primary_type" to "Test",
            "location" to "Test",
            "weakness" to arrayOf("None"),
            "height" to BigDecimal("1.0"),
            "weight" to BigDecimal("10.0")
        )
        assertFailsWith<ClassCastException> {
            Pokemon.fromMap(map)
        }
    }

    @Test
    fun `fromMap throws ClassCastException for incorrect type in map for Array`() {
        val map = mapOf<String, Any?>(
            "name" to "TypeErrArr",
            "base_stat" to 100,
            "primary_type" to "Test",
            "location" to "Test",
            "weakness" to "should_be_array", // Incorrect type
            "height" to BigDecimal("1.0"),
            "weight" to BigDecimal("10.0")
        )
        assertFailsWith<ClassCastException> {
            Pokemon.fromMap(map)
        }
    }
}
