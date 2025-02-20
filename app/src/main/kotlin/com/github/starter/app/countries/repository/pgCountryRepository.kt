package com.github.starter.app.countries.repository

import com.github.starter.app.config.JdbcClientFactory
import com.github.starter.app.countries.model.Countries
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@ConditionalOnProperty(prefix = "core", name = ["db"], havingValue = "pg")
@Repository
class pgCountryRepository @Autowired constructor(
    @Autowired clientFactory: JdbcClientFactory,
    @Value("\${flags.default-jdbc-client}") jdbcClientName: String
) : CountryRepository {
    private val client: DatabaseClient = clientFactory.forName(jdbcClientName).client()

    override fun listItems(): Flux<Countries> {
        return client.sql(
            """
                SELECT * FROM app.countries LIMIT 20;
            """.trimIndent()
        ).map(Mappers.Country()).all()
    }

}