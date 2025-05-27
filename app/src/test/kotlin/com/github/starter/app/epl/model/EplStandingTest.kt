package com.github.starter.app.epl.model

import com.github.starter.modules.epl.model.EplStanding
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EplStandingTest {
    @Test

    fun `EplStanding can be instantiated with all properties`() {
        val standing = EplStanding(
            season = 2023,
            ranking = 1,
            team = "Manchester City",
            played = 38,
            gf = 94,
            ga = 33,
            gd = 61,
            points = 89
        )
        assertEquals(2023, standing.season)
        assertEquals("Manchester City", standing.team)
        assertEquals(89, standing.points)
        assertEquals(1, standing.ranking)
    }

    @Test

    fun `EplStanding uses default values when not specified`() {
        val standingDefault = EplStanding(team = "Arsenal")
        assertEquals(0, standingDefault.season, "Default season should be 0.")
        assertEquals(0, standingDefault.ranking, "Default ranking should be 0.")
        assertEquals("Arsenal", standingDefault.team)
        assertEquals(0, standingDefault.played, "Default played should be 0.")
        assertEquals(0, standingDefault.gf, "Default gf should be 0.")
        assertEquals(0, standingDefault.ga, "Default ga should be 0.")
        assertEquals(0, standingDefault.gd, "Default gd should be 0.")
        assertEquals(0, standingDefault.points, "Default points should be 0.")
    }

    @Test

    fun `EplStanding instantiation with only required fields (if any) and relying on all defaults`() {
        val standingMinimal = EplStanding()
        assertEquals(0, standingMinimal.season)
        assertNull(standingMinimal.team)
        assertEquals(0, standingMinimal.points)
        assertEquals(0, standingMinimal.ranking)
    }

    @Test

    fun `equals and hashCode work correctly`() {
        val standing1 = EplStanding(2023, 1, "Man City", 38, 94, 33, 61, 89)
        val standing2 = EplStanding(2023, 1, "Man City", 38, 94, 33, 61, 89)
        val standing3 = EplStanding(2023, 2, "Arsenal", 38, 88, 42, 46, 86)
        assertEquals(standing1, standing2, "EplStanding instances with same properties should be equal.")
        assertNotEquals(standing1, standing3, "EplStanding instances with different properties should not be equal.")
        assertEquals(
            standing1.hashCode(),
            standing2.hashCode(),
            "Hash codes for equal EplStanding instances should be the same."
        )
    }

    @Test

    fun `toString returns a string representation`() {
        val standing = EplStanding(team = "Man City", points = 89, ranking = 1)
        val standingToString = standing.toString()
        assertTrue(
            standingToString.startsWith("EplStanding("),
            "toString should start with class name and opening parenthesis."
        )
        assertTrue(standingToString.contains("team=Man City"), "toString should contain team property.")
        assertTrue(standingToString.contains("points=89"), "toString should contain points property.")
        assertTrue(standingToString.contains("ranking=1"), "toString should contain ranking property.")
        assertTrue(standingToString.endsWith(")"), "toString should end with a closing parenthesis.")
    }

    @Test

    fun `copy creates a new instance with modified properties`() {
        val originalStanding = EplStanding(2023, 1, "Man City", 38, 94, 33, 61, 89)
        val copiedStanding = originalStanding.copy(ranking = 2, points = 85)
        assertNotSame(originalStanding, copiedStanding, "Copied EplStanding should be a new instance.")
        assertEquals(originalStanding.team, copiedStanding.team, "Unchanged properties should remain the same.")
        assertEquals(2, copiedStanding.ranking, "Ranking should be updated in the copied instance.")
        assertEquals(85, copiedStanding.points, "Points should be updated in the copied instance.")
        val sameCopy = originalStanding.copy()
        assertEquals(originalStanding, sameCopy)
        assertNotSame(originalStanding, sameCopy)
    }
}
