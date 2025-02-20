package com.github.starter.app.countries.endpoint

import com.github.starter.app.countries.model.Countries
import com.github.starter.app.countries.repository.CountryRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("country")
class CountriesEndpoint(private val countryRepository: CountryRepository) {

    @GetMapping("/all")
    fun list(): Flux<Countries> {
        return countryRepository.listItems()
    }

    @GetMapping("/search")
    fun listByRegion(
        @Argument("region") @RequestParam("region", defaultValue = "Asia") region: String
    ): Flux<Countries> {
        return countryRepository.listByRegion(region)
    }
}