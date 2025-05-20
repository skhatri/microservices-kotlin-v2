package com.github.starter.app.epl.repository

import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import com.github.starter.modules.epl.repository.Mappers
import io.r2dbc.spi.Readable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class MappersTest {

    @Mock
    private lateinit var readable: Readable

    @Test
    fun `eplMatch should map Readable to EplMatch correctly`() {
        val season = 2022
        val team = "Arsenal"
        val wk = 1
        val matchDate = LocalDate.now()
        val points = 3
        val gf = 2
        val ga = 1
        val result = "W"
        val opponent = "Chelsea"
        val venue = "Home"

        whenever(readable.get("season", Int::class.java)).thenReturn(season)
        whenever(readable.get("team", String::class.java)).thenReturn(team)
        whenever(readable.get("wk", Int::class.java)).thenReturn(wk)
        whenever(readable.get("matchDate", LocalDate::class.java)).thenReturn(matchDate)
        whenever(readable.get("points", Int::class.java)).thenReturn(points)
        whenever(readable.get("gf", Int::class.java)).thenReturn(gf)
        whenever(readable.get("ga", Int::class.java)).thenReturn(ga)
        whenever(readable.get("result", String::class.java)).thenReturn(result)
        whenever(readable.get("opponent", String::class.java)).thenReturn(opponent)
        whenever(readable.get("venue", String::class.java)).thenReturn(venue)

        val mapper = Mappers.eplMatch()
        val eplMatch = mapper.apply(readable)

        assertEquals(season, eplMatch.season)
        assertEquals(team, eplMatch.team)
        assertEquals(wk, eplMatch.wk)
        assertEquals(matchDate, eplMatch.matchDate)
        assertEquals(points, eplMatch.points)
        assertEquals(gf, eplMatch.gf)
        assertEquals(ga, eplMatch.ga)
        assertEquals(result, eplMatch.result)
        assertEquals(opponent, eplMatch.opponent)
        assertEquals(venue, eplMatch.venue)
    }

    @Test
    fun `eplMatch should handle nulls with defaults`() {
        whenever(readable.get("season", Int::class.java)).thenReturn(null) // Defaults to 0
        whenever(readable.get("team", String::class.java)).thenReturn(null) // No default, should be null
        whenever(readable.get("wk", Int::class.java)).thenReturn(null) // Defaults to 0
        whenever(readable.get("matchDate", LocalDate::class.java)).thenReturn(null) // No default
        whenever(readable.get("points", Int::class.java)).thenReturn(null) // Defaults to 0
        // ... other fields can be null too

        val mapper = Mappers.eplMatch()
        val eplMatch = mapper.apply(readable)

        assertEquals(0, eplMatch.season)
        assertNull(eplMatch.team)
        assertEquals(0, eplMatch.wk)
        assertNull(eplMatch.matchDate)
        assertEquals(0, eplMatch.points)
    }

    @Test
    fun `eplStanding should map Readable to EplStanding correctly`() {
        val season = 2023
        val played = 38
        val points = 90
        val team = "Man City"
        val ranking = 1
        val gf = 95
        val ga = 25
        val gd = 70

        whenever(readable.get("season", Int::class.java)).thenReturn(season)
        whenever(readable.get("played", Int::class.java)).thenReturn(played)
        whenever(readable.get("points", Int::class.java)).thenReturn(points)
        whenever(readable.get("team", String::class.java)).thenReturn(team)
        whenever(readable.get("ranking", Int::class.java)).thenReturn(ranking)
        whenever(readable.get("gf", Int::class.java)).thenReturn(gf)
        whenever(readable.get("ga", Int::class.java)).thenReturn(ga)
        whenever(readable.get("gd", Int::class.java)).thenReturn(gd)

        val mapper = Mappers.eplStanding()
        val eplStanding = mapper.apply(readable)

        assertEquals(season, eplStanding.season)
        assertEquals(played, eplStanding.played)
        assertEquals(points, eplStanding.points)
        assertEquals(team, eplStanding.team)
        assertEquals(ranking, eplStanding.ranking)
        assertEquals(gf, eplStanding.gf)
        assertEquals(ga, eplStanding.ga)
        assertEquals(gd, eplStanding.gd)
    }
    
    @Test
    fun `eplStanding should handle nulls with defaults`() {
        whenever(readable.get("season", Int::class.java)).thenReturn(null)
        whenever(readable.get("played", Int::class.java)).thenReturn(null)
        whenever(readable.get("points", Int::class.java)).thenReturn(null)
        whenever(readable.get("team", String::class.java)).thenReturn(null)
        whenever(readable.get("ranking", Int::class.java)).thenReturn(null)
        whenever(readable.get("gf", Int::class.java)).thenReturn(null)
        whenever(readable.get("ga", Int::class.java)).thenReturn(null)
        whenever(readable.get("gd", Int::class.java)).thenReturn(null)

        val mapper = Mappers.eplStanding()
        val eplStanding = mapper.apply(readable)
        
        assertEquals(0, eplStanding.season)
        assertEquals(0, eplStanding.played)
        assertEquals(0, eplStanding.points)
        assertNull(eplStanding.team)
        assertEquals(0, eplStanding.ranking)
        assertEquals(0, eplStanding.gf)
        assertEquals(0, eplStanding.ga)
        assertEquals(0, eplStanding.gd)
    }

    @Test
    fun `field with Class should map correctly`() {
        val teamName = "Liverpool"
        whenever(readable.get("team_name", String::class.java)).thenReturn(teamName)

        val mapper = Mappers.field("team_name", String::class.java)
        val result = mapper.apply(readable)

        assertEquals(teamName, result)
    }
    
    @Test
    fun `field with Class should return null if db value is null`() {
        whenever(readable.get("nullable_field", String::class.java)).thenReturn(null)

        val mapper = Mappers.field("nullable_field", String::class.java)
        val result: String? = mapper.apply(readable) // Explicitly type result as nullable

        assertNull(result)
    }

    @Test
    fun `field with Class and defaultValue should map correctly`() {
        val count = 5
        whenever(readable.get("count_val", Int::class.javaObjectType)).thenReturn(count) // Use object type for Int?

        val mapper = Mappers.field("count_val", Int::class.javaObjectType, 0)
        val result = mapper.apply(readable)

        assertEquals(count, result)
    }

    @Test
    fun `field with Class and defaultValue should use default if db value is null`() {
        val defaultValue = 0
        whenever(readable.get("count_val_nullable", Int::class.javaObjectType)).thenReturn(null)

        val mapper = Mappers.field("count_val_nullable", Int::class.javaObjectType, defaultValue)
        val result = mapper.apply(readable)

        assertEquals(defaultValue, result)
    }
}
