package com.github.starter.app.epl.model

import com.github.starter.modules.epl.model.EplMatch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EplMatchTest {
    private val testDate: LocalDate = LocalDate.of(2023, 8, 15)

    @Test

    fun `EplMatch can be instantiated with all properties`() {
        val match = EplMatch(
            season = 2023,
            wk = 1,
            matchDate = testDate,
            team = "Arsenal",
            opponent = "Nottm Forest",
            venue = "Home",
            result = "W",
            gf = 2,
            ga = 1,
            points = 3
        )
        assertEquals(2023, match.season)
        assertEquals("Arsenal", match.team)
        assertEquals(3, match.points)
        assertEquals(testDate, match.matchDate)
    }

    @Test

    fun `EplMatch uses default values when not specified`() {
        val matchDefault = EplMatch(team = "Spurs", opponent = "Brentford")
        assertEquals(0, matchDefault.season, "Default season should be 0.")
        assertEquals(0, matchDefault.wk, "Default wk should be 0.")
        assertNull(matchDefault.matchDate, "Default matchDate should be null.")
        assertEquals("Spurs", matchDefault.team)
        assertEquals("Brentford", matchDefault.opponent)
        assertNull(matchDefault.venue, "Default venue should be null.")
        assertNull(matchDefault.result, "Default result should be null.")
        assertEquals(0, matchDefault.gf, "Default gf should be 0.")
        assertEquals(0, matchDefault.ga, "Default ga should be 0.")
        assertEquals(0, matchDefault.points, "Default points should be 0.")
    }

    @Test

    fun `EplMatch instantiation with only required fields (if any) and relying on all defaults`() {
        val matchMinimal = EplMatch()
        assertEquals(0, matchMinimal.season)
        assertNull(matchMinimal.team)
        assertEquals(0, matchMinimal.points)
        assertNull(matchMinimal.matchDate)
    }

    @Test

    fun `equals and hashCode work correctly`() {
        val match1 = EplMatch(2023, 1, testDate, "Arsenal", "Nottm Forest", "Home", "W", 2, 1, 3)
        val match2 = EplMatch(2023, 1, testDate, "Arsenal", "Nottm Forest", "Home", "W", 2, 1, 3)
        val match3 = EplMatch(2023, 1, testDate, "Chelsea", "Liverpool", "Away", "D", 1, 1, 1)
        assertEquals(match1, match2, "EplMatch instances with same properties should be equal.")
        assertNotEquals(match1, match3, "EplMatch instances with different properties should not be equal.")
        assertEquals(
            match1.hashCode(),
            match2.hashCode(),
            "Hash codes for equal EplMatch instances should be the same."
        )
    }

    @Test

    fun `toString returns a string representation`() {
        val match = EplMatch(team = "Arsenal", opponent = "Nottm Forest", points = 3)
        val matchToString = match.toString()
        assertTrue(
            matchToString.startsWith("EplMatch("),
            "toString should start with class name and opening parenthesis."
        )
        assertTrue(matchToString.contains("team=Arsenal"), "toString should contain team property.")
        assertTrue(matchToString.contains("opponent=Nottm Forest"), "toString should contain opponent property.")
        assertTrue(matchToString.contains("points=3"), "toString should contain points property.")
        assertTrue(matchToString.endsWith(")"), "toString should end with a closing parenthesis.")
    }

    @Test

    fun `copy creates a new instance with modified properties`() {
        val originalMatch = EplMatch(2023, 1, testDate, "Arsenal", "Nottm Forest", "Home", "W", 2, 1, 3)
        val copiedMatch = originalMatch.copy(result = "L", points = 0, gf = 1, ga = 2)
        assertNotSame(originalMatch, copiedMatch, "Copied EplMatch should be a new instance.")
        assertEquals(originalMatch.team, copiedMatch.team, "Unchanged properties should remain the same.")
        assertEquals("L", copiedMatch.result, "Result should be updated in the copied instance.")
        assertEquals(0, copiedMatch.points, "Points should be updated in the copied instance.")
        assertEquals(1, copiedMatch.gf)
        assertEquals(2, copiedMatch.ga)
        val sameCopy = originalMatch.copy()
        assertEquals(originalMatch, sameCopy)
        assertNotSame(originalMatch, sameCopy)
    }
}
