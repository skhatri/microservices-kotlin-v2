package com.github.starter.modules.epl.endpoint

import com.github.starter.app.epl.model.Team
import com.github.starter.core.container.Container
import com.github.starter.core.container.TokenInformation
import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import com.github.starter.modules.epl.repository.MatchRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("epl")
class EplEndpoint(private val matchRepository: MatchRepository) {
    @GetMapping("/biggest-margin")

    fun biggestMargin(@Argument("season") @RequestParam("season", defaultValue = "-1") season: Int): Flux<EplMatch> {
        return matchRepository.biggestMargin(season)
    }

    @GetMapping("/most-goals")

    fun mostGoals(@Argument("season") @RequestParam("season", defaultValue = "-1") season: Int): Flux<EplMatch> {
        return matchRepository.mostGoalsScored(season)
    }

    @GetMapping("/season-performance")

    fun seasonPerformance(
        @Argument("team") @RequestParam("team") team: String,
        @Argument("season") @RequestParam("season", defaultValue = "-1") season: Int
    ): Flux<EplStanding> {
        return matchRepository.seasonPerformance(team, season)
    }

    @GetMapping("/season-table")

    fun seasonTable(@Argument("season") @RequestParam("season") season: Int): Flux<EplStanding> {
        return matchRepository.seasonTable(season)
    }

    @GetMapping("/winners")

    fun winner(@Argument("season") @RequestParam("season", defaultValue = "-1") season: Int): Flux<EplStanding> {
        return matchRepository.winner(season)
    }

    @GetMapping("/all-teams")

    fun allTeams(
        @Argument("season") @RequestParam("season", defaultValue = "-1") season: Int,
        @RequestParam(
            name = "nextToken",
            defaultValue = ""
        ) pageStateToken: String
    ): Mono<Container<List<Team>>> {
        val (start, limit) = TokenInformation.decode(pageStateToken)
        return matchRepository.allTeams(season, start, limit).collectList()
            .map { items ->
                Container(items, TokenInformation.createPageMeta(items.size, start, limit))
            }
    }
}
