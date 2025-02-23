package com.github.starter.core.container

import java.util.*

class TokenInformation {
    companion object {
        fun encode(start: Int, limit: Int): String {
            return Base64.getEncoder().encodeToString("${start + limit}:${limit}".toByteArray())
        }

        fun decode(data: String): Pair<Int, Int> {
            var limit = 10
            var start = 0
            if (data != "") {
                val tokenData = String(Base64.getDecoder().decode(data)).split(":")
                start = tokenData.get(0).toInt()
                limit = tokenData.get(1).toInt()
            }
            return Pair(start, limit)

        }

        fun createPageMeta(recordCount: Int, start: Int, limit: Int): Map<String, Any> {
            return if (recordCount == limit) {
                val next = TokenInformation.encode(start + limit, limit)
                mapOf("nextToken" to next)
            } else {
                mapOf()
            }
        }
    }
}