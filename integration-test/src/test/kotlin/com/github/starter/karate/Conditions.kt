@file:JvmName("Conditions")

package com.github.starter.karate;

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Conditions {
    companion object {
        @JvmStatic
        fun ignoreMock(): Boolean = checkEnv("NO_WIREMOCK")

        private fun checkEnv(envName: String): Boolean {
            return System.getenv(envName)?.toBoolean() ?: false
        }

        @JvmStatic
        fun ignoreBoot(): Boolean = checkEnv("NO_BOOT")

        @JvmStatic
        fun serverNotRunning(): Boolean {
            return try {
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/health"))
                    .GET()
                    .build()
                val response = client.send(request, HttpResponse.BodyHandlers.discarding())
                response.statusCode() != 200
            } catch (e: Exception) {
               true
            }
        }
    }
}
