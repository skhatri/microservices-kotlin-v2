package com.github.starter.modules.epl.repository;

import com.github.starter.app.epl.model.Team
import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import reactor.core.publisher.Flux

interface MatchRepository {
    fun biggestMargin(season: Int): Flux<EplMatch>

    fun mostGoalsScored(season: Int): Flux<EplMatch>

    fun seasonPerformance(team: String, season: Int): Flux<EplStanding>

    fun seasonTable(season: Int): Flux<EplStanding>

    fun winner(season: Int): Flux<EplStanding>

    fun allTeams(season: Int, start: Int, limit: Int): Flux<Team>
}
