package com.github.starter.app.pokemon.repository

import com.github.pokemon.model.Pokemon
import com.github.starter.app.config.JdbcClientFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
open class PokemonRepository(
@Autowired clientFactory: JdbcClientFactory,
@Value("\${flags.default-jdbc-client}") jdbcClientName: String) {
private val client: DatabaseClient = clientFactory.forName(jdbcClientName).client()

fun listEntries(start: Int = 0, limit: Int = 10): Flux<Pokemon> {
    return client.sql(
        """select name, primary_type, secondary_type, base_stat, location, legendary, weakness, height, weight
        |from app.pokemons
        |ORDER BY name asc, primary_type desc
        |limit $limit
        |offset $start
        """.trimMargin())
    .mapProperties(Pokemon::class.java).all()
    }
}