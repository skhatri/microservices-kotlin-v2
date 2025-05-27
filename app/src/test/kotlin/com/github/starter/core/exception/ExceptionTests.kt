package com.github.starter.core.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Exception Tests")
class ExceptionTests {
    @Test
    @DisplayName("should create BadRequest exception with message and code")
    fun `should create BadRequest exception with message and code`() {
        val message = "Invalid request data"
        val code = "BAD_REQUEST_001"
        val exception = BadRequest(code, message)
        assertEquals(code, exception.message) { "Exception message should be the code" }
        assertEquals(code, exception.code) { "Exception code should match" }
        assertEquals(message, exception.summary) { "Exception summary should match the descriptive message" }
    }

    @Test
    @DisplayName("should create BadRequest exception with default values")
    fun `should create BadRequest exception with default values`() {
        val exception = BadRequest()
        assertNotNull(exception.message) { "Exception should have a message" }
        assertNotNull(exception.code) { "Exception should have a code" }
        assertNotNull(exception.summary) { "Exception should have a summary" }
    }

    @Test
    @DisplayName("should create InternalServerError exception")
    fun `should create InternalServerError exception`() {
        val code = "INTERNAL_ERROR_001"
        val message = "Internal server error occurred"
        val exception = InternalServerError(code, message)
        assertEquals(code, exception.message) { "Exception message should be the code" }
        assertEquals(code, exception.code) { "Exception code should match" }
        assertEquals(message, exception.summary) { "Exception summary should match the descriptive message" }
        assertTrue(exception is ApiException) { "Should be an ApiException" }
    }

    @Test
    @DisplayName("should handle exception inheritance properly")

    fun `should handle exception inheritance properly`() {
        val badRequest = BadRequest("BR001", "Bad request")
        val serverError = InternalServerError("SE001", "Server error")
        assertTrue(badRequest is ApiException) { "BadRequest should be an ApiException" }
        assertTrue(serverError is ApiException) { "InternalServerError should be an ApiException" }
        assertTrue(badRequest is RuntimeException) { "BadRequest should be a RuntimeException" }
        assertTrue(serverError is RuntimeException) { "InternalServerError should be a RuntimeException" }
    }
}
