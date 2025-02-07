package com.github.starter.app.pokemon.repository

import com.github.pokemon.model.Pokemon
import com.github.starter.app.config.JdbcClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
open class EntriesRepository(
    @Autowired clientFactory: JdbcClientFactory,
    @Value("\${flags.default-jdbc-client}") jdbcClientName: String
) {
    private val client: DatabaseClient = clientFactory.forName(jdbcClientName).client()

    fun listEntries(): Flux<Pokemon> {
        return client.sql("select * from app.pokemons").mapProperties(Pokemon::class.java).all()
    }
}