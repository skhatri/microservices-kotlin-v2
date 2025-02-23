package com.github.starter.modules.epl.repository

import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import com.github.starter.app.config.JdbcClientFactory
import com.github.starter.app.epl.model.Team
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@ConditionalOnProperty(prefix = "core", name = ["db"], havingValue = "pg")
@Repository
class PgMatchRepository @Autowired constructor(
    @Autowired clientFactory: JdbcClientFactory,
    @Value("\${flags.default-jdbc-client}") jdbcClientName: String
) : MatchRepository {
    private val client: DatabaseClient = clientFactory.forName(jdbcClientName).client()

    override fun biggestMargin(season: Int): Flux<EplMatch> {
        val seasonSelector = getSeasonSelector(season)
        return client.sql(
            """
                SELECT * 
                FROM app.epl_team_match 
                WHERE season $seasonSelector AND points > 0 AND abs(gf-ga) = (
                    SELECT max(abs(gf-ga)) 
                    FROM app.epl_team_match
                    WHERE season $seasonSelector
                )
            """.trimIndent()
        ).map(Mappers.eplMatch()).all()
    }

    override fun mostGoalsScored(season: Int): Flux<EplMatch> {
        val seasonSelector = getSeasonSelector(season)
        return client.sql(
            """
                SELECT * 
                FROM app.epl_team_match 
                WHERE season $seasonSelector AND points > 0 AND gf = (
                    SELECT max(gf) 
                    FROM app.epl_team_match
                    WHERE season $seasonSelector
                )
            """.trimIndent()
        ).map(Mappers.eplMatch()).all()
    }

    override fun seasonPerformance(team: String, season: Int): Flux<EplStanding> {
        val seasonSelector = getSeasonSelector(season)
        return client.sql(
            """
                SELECT *
                FROM app.epl_standings
                WHERE season $seasonSelector AND team = $1
                ORDER BY season ASC
            """.trimIndent()
        ).bind("$1", team).map(Mappers.eplStanding()).all()
    }

    override fun seasonTable(season: Int): Flux<EplStanding> {
        return client.sql(
            """
                SELECT *
                FROM app.epl_standings
                WHERE season = $1 
                ORDER BY ranking ASC
            """.trimIndent()
        ).bind("$1", season).map(Mappers.eplStanding()).all()
    }

    override fun winner(season: Int): Flux<EplStanding> {
        val seasonSelector = getSeasonSelector(season)
        return client.sql(
            """
                SELECT *
                FROM app.epl_standings
                WHERE season $seasonSelector AND ranking = 1
                ORDER BY season ASC
            """.trimIndent()
        ).map(Mappers.eplStanding()).all()
    }

    override fun allTeams(season: Int, start: Int, limit: Int): Flux<Team> {
        val seasonSelector = getSeasonSelector(season)
        return client.sql(
            """
                SELECT DISTINCT team
                FROM app.epl_standings
                WHERE season $seasonSelector
                ORDER BY team ASC
                LIMIT $limit
                OFFSET $start
            """.trimIndent()
        ).map(Mappers.field("team", String::class.java, "")).all().map { Team(it) }
    }

    companion object {
        private fun getSeasonSelector(season: Int): String {
            return if (season != -1) "= $season" else "<> -1"
        }
    }
}
