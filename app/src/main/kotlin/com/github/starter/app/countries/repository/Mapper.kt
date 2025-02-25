package com.github.starter.app.countries.repository

import com.github.starter.app.countries.model.Countries
import io.r2dbc.spi.Readable
import java.util.function.Function

object Mappers {
    fun Country(): Function<Readable, Countries> {
        return Function { kv ->
            Countries(
                name = kv.get("name", String::class.java),
                capital = kv.get("capital", String::class.java),
                population = kv.get("population", Int::class.java) ?: 0,
                area = kv.get("area", Float::class.java),
                region = kv.get("region", String::class.java)
            )
        }
    }
}