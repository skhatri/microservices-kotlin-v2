package com.github.starter.app.pokemon.endpoint

import com.github.pokemon.model.Pokemon
import com.github.starter.app.pokemon.repository.PokemonRepository
import com.github.starter.core.container.Container
import com.github.starter.core.container.TokenInformation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pokemon")
open class EntriesEndpoint(private val pokemonRepository: PokemonRepository) {

    @GetMapping("/list")
    fun listEntries(
        @RequestParam(
            name = "nextToken",
            defaultValue = ""
        ) pageStateToken: String
    ): Mono<Container<List<Pokemon>>> {
        val (start, limit) = TokenInformation.decode(pageStateToken)

        return pokemonRepository.listEntries(start, limit).collectList()
            .map { items ->
                Container(items, TokenInformation.createPageMeta(items.size, start, limit))
            }
    }

}
