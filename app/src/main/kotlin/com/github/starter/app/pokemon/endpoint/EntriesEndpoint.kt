package com.github.starter.app.pokemon.endpoint

import com.github.pokemon.model.Pokemon
import com.github.starter.app.pokemon.repository.EntriesRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@RestController
@RequestMapping("/pokemon")
open class EntriesEndpoint(private val entriesRepository: EntriesRepository) {

    @GetMapping("/list")
    fun listEntries(): Flux<Pokemon> = entriesRepository.listEntries()

}
