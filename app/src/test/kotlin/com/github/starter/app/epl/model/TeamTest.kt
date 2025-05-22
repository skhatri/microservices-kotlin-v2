package com.github.starter.app.epl.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TeamTest {

    @Test
    fun `Team can be instantiated`() {
        val teamName = "Arsenal"
        val team = Team(name = teamName)
        assertEquals(teamName, team.name)
    }

    @Test
    fun `equals and hashCode work correctly`() {
        val team1 = Team(name = "Arsenal")
        val team2 = Team(name = "Arsenal")
        val team3 = Team(name = "Chelsea")

        assertEquals(team1, team2, "Teams with the same name should be equal.")
        assertNotEquals(team1, team3, "Teams with different names should not be equal.")

        assertEquals(team1.hashCode(), team2.hashCode(), "Hash codes for equal teams should be the same.")
        // Hash codes for unequal teams are not strictly required to be different, but usually are.
    }

    @Test
    fun `toString returns a string representation`() {
        val team = Team(name = "Arsenal")
        val teamToString = team.toString()

        assertTrue(teamToString.contains("Team"), "toString should contain the class name.")
        assertTrue(teamToString.contains("name=Arsenal"), "toString should contain the property and its value.")
    }

    @Test
    fun `copy creates a new instance with modified properties`() {
        val originalTeam = Team(name = "Arsenal")
        val copiedTeam = originalTeam.copy(name = "Manchester United")

        assertNotEquals(originalTeam, copiedTeam, "Copied team should be a different instance if property changed.")
        assertEquals("Arsenal", originalTeam.name, "Original team name should remain unchanged.")
        assertEquals("Manchester United", copiedTeam.name, "Copied team should have the new name.")

        val sameCopy = originalTeam.copy()
        assertEquals(originalTeam, sameCopy, "Copy with no changes should be equal to original.")
        assertSame(originalTeam.name, sameCopy.name, "Name reference should be same if not changed in copy.")
    }
}
