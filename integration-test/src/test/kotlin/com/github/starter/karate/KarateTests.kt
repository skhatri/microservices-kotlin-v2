package com.github.starter.karate

import com.intuit.karate.junit5.Karate
import org.junit.jupiter.api.condition.DisabledIf

@DisabledIf("com.github.starter.karate.Conditions#serverNotRunning")
class KarateTests {

    @Karate.Test
    fun dummyTest(): Karate = Karate.run("classpath:karate/placeholder.feature")

    @Karate.Test
    fun statusTest(): Karate = Karate.run("classpath:karate/status.feature")

    @Karate.Test
    fun pokemonTest(): Karate = Karate.run("classpath:karate/pokemon/pokemon.feature")
}
