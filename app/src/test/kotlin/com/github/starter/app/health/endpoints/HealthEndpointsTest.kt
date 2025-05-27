package com.github.starter.app.health.endpoints

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@DisplayName("Health Endpoints Tests")
class HealthEndpointsTest {
    @Test
    @DisplayName("should return health status successfully")

    fun `should return health status successfully`() {
        val healthEndpoints = HealthEndpoints("test-app")
        val webTestClient = WebTestClient
            .bindToController(healthEndpoints)
            .build()
        webTestClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .jsonPath("$.app_name").isEqualTo("test-app")
    }

    @Test
    @DisplayName("should return liveness check")

    fun `should return liveness check`() {
        val healthEndpoints = HealthEndpoints("test-app")
        val webTestClient = WebTestClient
            .bindToController(healthEndpoints)
            .build()
        webTestClient.get()
            .uri("/liveness")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("live")
            .jsonPath("$.message").isEqualTo("is running!")
    }

    @Test
    @DisplayName("should return readiness check")

    fun `should return readiness check`() {
        val healthEndpoints = HealthEndpoints("test-app")
        val webTestClient = WebTestClient
            .bindToController(healthEndpoints)
            .build()
        webTestClient.get()
            .uri("/readiness")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("ready")
            .jsonPath("$.message").isEqualTo("can serve!")
    }

    @Test
    @DisplayName("should handle reactive health check")

    fun `should handle reactive health check`() {
        val healthEndpoints = HealthEndpoints("reactive-app")
        val livenessResult = healthEndpoints.liveness()
        StepVerifier.create(livenessResult)
            .expectNextMatches { response ->
                response["status"] == "live" && response["message"] == "is running!"
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("should provide consistent responses")

    fun `should provide consistent responses`() {
        val healthEndpoints = HealthEndpoints("consistent-app")
        val firstCall = healthEndpoints.health()
        val secondCall = healthEndpoints.health()
        assertNotNull(firstCall) { "First health call should return result" }
        assertNotNull(secondCall) { "Second health call should return result" }
        assertEquals(firstCall["app_name"], secondCall["app_name"]) { "App names should be consistent" }
        assertEquals(firstCall["status"], secondCall["status"]) { "Status should be consistent" }
    }
}
