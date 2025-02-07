package com.github.starter;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = arrayOf(R2dbcAutoConfiguration::class))
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args);
}

