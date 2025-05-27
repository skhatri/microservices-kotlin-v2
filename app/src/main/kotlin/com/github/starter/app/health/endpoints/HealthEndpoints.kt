package com.github.starter.app.health.endpoints;

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController(value = "/")
class HealthEndpoints(@Value("\${spring.application.name}") val name: String) {
    @GetMapping("/")

    fun index(): Mono<Map<String, Any>> {
        return createPayload("up", "Journey starts here!");
    }

    @GetMapping("/favicon.ico")

    fun favicon(): Mono<Void> {
        return Mono.empty();
    }

    @GetMapping("/liveness")

    fun liveness(): Mono<Map<String, Any>> {
        return createPayload("live", "is running!");
    }

    @GetMapping("/readiness")

    fun readiness(): Mono<Map<String, Any>> {
        return createPayload("ready", "can serve!");
    }

    @GetMapping("/health")

    fun health(): Map<String, String> {
        return statusMap()
    }

    @GetMapping("/health-stream")

    fun streamHealth(): Flux<Map<String, String>> {
        return Flux.interval(Duration.ofSeconds(1))
            .map { n ->
                val response: MutableMap<String, String> =
                    HashMap<String, String>()
                response.put("id", java.lang.String.format("%d", n))
                response.putAll(statusMap())
                response
            }
    }

    private fun createPayload(status: String, message: String): Mono<Map<String, Any>> {
        return Mono.just(mapOf("status" to status, "message" to message));
    }

    private fun statusMap(): Map<String, String> {
        return java.util.Map.of<String, String>(
            "app_name", name,
            "status", "UP",
            "server_time", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
}
