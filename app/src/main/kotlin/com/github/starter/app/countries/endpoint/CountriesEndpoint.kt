package com.github.starter.app.countries.endpoint

import com.github.starter.app.countries.model.Countries
import com.github.starter.app.countries.repository.CountryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("country")
class CountriesEndpoint(private val countryRepository: CountryRepository) {

    @GetMapping("/search")
    fun list(): Flux<Countries> {
        return countryRepository.listItems()
    }
}