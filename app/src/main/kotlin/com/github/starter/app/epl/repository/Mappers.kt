package com.github.starter.modules.epl.repository

import com.github.starter.modules.epl.model.EplMatch
import com.github.starter.modules.epl.model.EplStanding
import io.r2dbc.spi.Readable
import java.time.LocalDate
import java.util.function.Function

object Mappers {
    fun eplMatch(): Function<Readable, EplMatch> {
        return Function { kv ->
            EplMatch(
                season = kv.get("season", Int::class.java) ?: 0,
                team = kv.get("team", String::class.java),
                wk = kv.get("wk", Int::class.java) ?: 0,
                matchDate = kv.get("matchDate", LocalDate::class.java),
                points = kv.get("points", Int::class.java) ?: 0,
                gf = kv.get("gf", Int::class.java) ?: 0,
                ga = kv.get("ga", Int::class.java) ?: 0,
                result = kv.get("result", String::class.java),
                opponent = kv.get("opponent", String::class.java),
                venue = kv.get("venue", String::class.java)
            )
        }
    }

    fun eplStanding(): Function<Readable, EplStanding> {
        return Function { kv ->
            EplStanding(
                season = kv.get("season", Int::class.java) ?: 0,
                played = kv.get("played", Int::class.java) ?: 0,
                points = kv.get("points", Int::class.java) ?: 0,
                team = kv.get("team", String::class.java),
                ranking = kv.get("ranking", Int::class.java) ?: 0,
                gf = kv.get("gf", Int::class.java) ?: 0,
                ga = kv.get("ga", Int::class.java) ?: 0,
                gd = kv.get("gd", Int::class.java) ?: 0
            )
        }
    }

    fun <T> field(key: String, cls: Class<T>): Function<Readable, T> {
        return Function { kv -> kv.get(key, cls) }
    }

    fun <T> field(key: String, cls: Class<T>, defaultValue: T): Function<Readable, T> {
        return Function { kv -> kv.get(key, cls) ?: defaultValue }
    }
}
