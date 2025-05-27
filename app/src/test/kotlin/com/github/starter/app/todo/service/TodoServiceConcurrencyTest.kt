package com.github.starter.app.todo.service

import com.github.starter.app.todo.model.TodoTask
import com.github.starter.core.testing.FakeTodoRepository
import com.github.starter.core.testing.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentLinkedQueue
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Todo Service Concurrency")
internal class TodoServiceConcurrencyTest {
    private lateinit var todoRepository: FakeTodoRepository
    private lateinit var todoService: DefaultTodoService

    @BeforeEach

    internal fun setUp() {
        todoRepository = FakeTodoRepository()
        todoService = DefaultTodoService(todoRepository)
    }

    @Nested
    @DisplayName("Concurrent Read Operations")
    internal inner class ConcurrentReadOperations {
        @Test
        @DisplayName("should handle multiple concurrent reads without interference")

        internal fun shouldHandleMultipleConcurrentReadsWithoutInterference() {
            val todoTask = TestDataBuilder.createTodoTask(id = "test-id")
            todoRepository.addItem(todoTask)
            val threadCount = 20
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)
            val results = ConcurrentLinkedQueue<TodoTask>()
            val errors = ConcurrentLinkedQueue<Throwable>()
            repeat(threadCount) {
                executor.execute {
                    try {
                        val result = todoService.findById("test-id")
                            .timeout(Duration.ofSeconds(5))
                            .block()
                        result?.let { results.offer(it) }
                    } catch (e: Throwable) {
                        errors.offer(e)
                    } finally {
                        latch.countDown()
                    }
                }
            }
            assertThat(latch.await(10, TimeUnit.SECONDS))
                .withFailMessage("Timeout waiting for concurrent operations to complete")
                .isTrue()
            executor.shutdown()
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue()
            assertThat(errors).isEmpty()
            assertThat(results).hasSize(threadCount)
            assertThat(results).allMatch { it.id == "test-id" }
        }

        @Test
        @DisplayName("should maintain reactive stream behavior under concurrent subscriptions")

        internal fun shouldMaintainReactiveStreamBehaviorUnderConcurrentSubscriptions() {
            val todoList = TestDataBuilder.createTodoList(2)
            todoList.forEach { todoRepository.addItem(it) }

            val subscriberCount = 10
            val latch = CountDownLatch(subscriberCount)
            val results = ConcurrentLinkedQueue<List<TodoTask>>()
            val sharedMono = todoService.listItems().cache()
            repeat(subscriberCount) {
                sharedMono.subscribe(
                    { result ->
                        results.offer(result)
                        latch.countDown()
                    },
                    { error ->
                        fail("Unexpected error: ${error.message}")
                        latch.countDown()
                    }
                )
            }
            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue()
            assertThat(results).hasSize(subscriberCount)
            assertThat(results).allMatch { it.size == 2 }
        }
    }

    @Nested
    @DisplayName("Concurrent Write Operations")
    internal inner class ConcurrentWriteOperations {
        @Test
        @DisplayName("should handle concurrent save operations correctly")

        internal fun shouldHandleConcurrentSaveOperationsCorrectly() {
            val executor = Executors.newFixedThreadPool(10)
            val saveCount = 20
            val latch = CountDownLatch(saveCount)
            val savedTodos = ConcurrentLinkedQueue<TodoTask>()
            repeat(saveCount) { index ->
                executor.execute {
                    try {
                        val todo = TestDataBuilder.createTodoTask(
                            id = "task-$index",
                            description = "Task-$index"
                        )
                        val result = todoService.save(todo)
                            .timeout(Duration.ofSeconds(5))
                            .block()
                        result?.let { savedTodos.offer(it) }
                    } finally {
                        latch.countDown()
                    }
                }
            }
            assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue()
            executor.shutdown()
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue()
            assertThat(savedTodos).hasSize(saveCount)
            assertThat(todoRepository.size()).isEqualTo(saveCount)
        }
    }

    @Nested
    @DisplayName("Memory and Resource Management")
    internal inner class MemoryAndResourceManagement {
        @Test
        @DisplayName("should not leak memory under high concurrent load")

        internal fun shouldNotLeakMemoryUnderHighConcurrentLoad() {
            val initialMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

            val todoTask = TestDataBuilder.createTodoTask(id = "memory-test")
            todoRepository.addItem(todoTask)
            val requestCount = 1000
            val threadCount = 50
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(requestCount)
            repeat(requestCount) { index ->
                executor.execute {
                    try {
                        todoService.findById("memory-test")
                            .timeout(Duration.ofMillis(100))
                            .block()
                    } catch (e: Exception) {
                    } finally {
                        latch.countDown()
                    }
                }
            }
            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue()
            executor.shutdown()
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue()
            System.gc()
            Thread.sleep(100)
            val finalMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

            val memoryIncrease = finalMemory - initialMemory
            assertThat(memoryIncrease)
                .withFailMessage("Memory usage increased by ${memoryIncrease / 1024 / 1024}MB, which may indicate a memory leak")
                .isLessThan(50 * 1024 * 1024)
        }

        @Test
        @DisplayName("should handle high frequency updates without resource exhaustion")

        internal fun shouldHandleHighFrequencyUpdatesWithoutResourceExhaustion() {
            val updateCount = 500
            val executor = Executors.newFixedThreadPool(20)
            val latch = CountDownLatch(updateCount)
            val successCount = AtomicInteger(0)
            repeat(updateCount) { index ->
                executor.execute {
                    try {
                        val task = TestDataBuilder.createTodoTask(id = "update-task-$index")
                        todoService.update("update-task-$index", task)
                            .timeout(Duration.ofSeconds(2))
                            .block()
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                    } finally {
                        latch.countDown()
                    }
                }
            }
            assertThat(latch.await(20, TimeUnit.SECONDS)).isTrue()
            executor.shutdown()
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue()
            assertThat(successCount.get())
                .withFailMessage("Expected at least 80% success rate, got ${successCount.get()}/$updateCount")
                .isGreaterThan((updateCount * 0.8).toInt())
        }
    }
} 
