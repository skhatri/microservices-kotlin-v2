package com.github.starter.app.countries.repository

import com.github.starter.app.countries.model.Countries
import reactor.core.publisher.Flux

interface CountryRepository {
    fun listItems(): Flux<Countries>
    fun listByRegion(region: String): Flux<Countries>
}