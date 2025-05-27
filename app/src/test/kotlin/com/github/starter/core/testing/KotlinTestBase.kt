package com.github.starter.core.testing

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Duration
import kotlin.test.fail
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KotlinTestBase {
    protected val defaultTimeout = Duration.ofSeconds(5)

    @BeforeEach
    open fun setUp() {
    }

    @AfterEach
    open fun tearDown() {
    }

    protected fun loadJsonFromResources(path: String): String {
        val classLoader = this::class.java.classLoader
        val inputStream = classLoader.getResourceAsStream(path)
            ?: fail("Resource not found: $path")
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.readText()
        }
    }

    protected fun String.shouldBeValidJsonWith(vararg expectedKeys: String) {
        assertTrue(this.isNotEmpty(), "JSON should not be empty")
        assertTrue(this.trim().startsWith("{") || this.trim().startsWith("["), "Should be valid JSON")
        expectedKeys.forEach { key ->
            assertTrue(this.contains("\"$key\""), "JSON should contain key: $key")
        }
    }

    protected fun <T> Mono<T>.verifyWith(
        timeout: Duration = defaultTimeout,
        verify: StepVerifier.Step<T>.() -> StepVerifier
    ): StepVerifier {
        return StepVerifier.create(this)
            .let(verify)
    }

    protected fun <T> Flux<T>.verifyWith(
        timeout: Duration = defaultTimeout,
        verify: StepVerifier.Step<T>.() -> StepVerifier
    ): StepVerifier {
        return StepVerifier.create(this)
            .let(verify)
    }

    protected fun <T> Mono<T>.shouldComplete() {
        StepVerifier.create(this)
            .verifyComplete()
    }

    protected inline fun <T, reified E : Throwable> Mono<T>.shouldFailWith() {
        StepVerifier.create(this)
            .expectError(E::class.java)
            .verify()
    }

    protected inline fun <reified T> createTestData(
        builder: () -> T
    ): T = builder()

    protected inline fun <reified T> createTestDataList(
        size: Int,
        factory: (Int) -> T
    ): List<T> = (0 until size).map(factory)

    protected fun <T> Collection<T>.shouldContainExactlyInOrder(vararg expected: T) {
        assertEquals(expected.size, this.size, "Collection size should match expected")
        expected.forEachIndexed { index, expectedItem ->
            assertEquals(expectedItem, this.elementAt(index), "Element at index $index should match")
        }
    }

    protected fun String.shouldContainAndNotBeEmpty(expected: String) {
        assertTrue(this.isNotEmpty(), "String should not be empty")
        assertTrue(this.contains(expected), "String should contain: $expected")
    }

    protected inline fun <reified T> Any?.shouldBeOfType(): T {
        assertNotNull(this, "Object should not be null")
        assertTrue(this is T, "Object should be of type ${T::class.simpleName}")
        return this as T
    }

    protected fun assertThat(condition: Boolean, message: String) {
        assertTrue(condition, message)
    }

    protected fun <T> assertThat(actual: T, expected: T, message: String) {
        assertEquals(expected, actual, message)
    }

    protected fun <T> assertNotNull(value: T?, message: String = "Value should not be null"): T {
        assertNotNull(value, message)
        return value!!
    }

    protected fun timeout(seconds: Long): Duration = Duration.ofSeconds(seconds)

    protected fun timeoutMs(milliseconds: Long): Duration = Duration.ofMillis(milliseconds)

    protected fun generateTestData(count: Int = 100): List<Int> = (1..count).toList()

    protected fun <T> forAll(data: List<T>, property: (T) -> Boolean) {
        data.forEach { item ->
            assertTrue(property(item), "Property should hold for all test data")
        }
    }
} 
