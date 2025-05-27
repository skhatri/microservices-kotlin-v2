package com.github.starter.app.todo.service

import com.github.starter.app.todo.model.TodoTask
import com.github.starter.core.exception.BadRequest
import com.github.starter.core.testing.FakeTodoRepository
import com.github.starter.core.testing.KotlinTestBase
import com.github.starter.core.testing.TestDataBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import reactor.test.StepVerifier
import java.time.LocalDateTime
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.assertj.core.api.Assertions.assertThat

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Default Todo Service")
internal class DefaultTodoServiceTest : KotlinTestBase() {
    private lateinit var todoRepository: FakeTodoRepository
    private lateinit var todoService: DefaultTodoService

    @BeforeEach
    override open fun setUp() {
        todoRepository = FakeTodoRepository()
        todoService = DefaultTodoService(todoRepository)
    }

    @Nested
    @DisplayName("Save Operations")
    internal inner class SaveOperations {
        @Test
        @DisplayName("should save valid todo task successfully")

        internal fun `should save valid todo task successfully`() {
            val task = TestDataBuilder.createTodoTask()
            StepVerifier.create(todoService.save(task))
                .expectNextMatches { saved ->
                    saved.description == task.description &&
                            saved.actionBy == task.actionBy &&
                            saved.status == task.status
                }
                .verifyComplete()
            assertThat(todoRepository.size()).isEqualTo(1)
        }

        @Test
        @DisplayName("should handle repository failure gracefully")

        internal fun `should handle repository failure gracefully`() {
            val task = TestDataBuilder.createTodoTask()
            todoRepository.setShouldFailAdd(true)
            StepVerifier.create(todoService.save(task))
                .expectError(RuntimeException::class.java)
                .verify()
        }

        @Test
        @DisplayName("should reject empty description")

        internal fun `should reject empty description`() {
            val taskWithEmptyDescription = TestDataBuilder.createTodoTask(description = "")
            StepVerifier.create(todoService.save(taskWithEmptyDescription))
                .expectError(BadRequest::class.java)
                .verify()
        }

        @Test
        @DisplayName("should reject null or blank actionBy")

        internal fun `should reject null or blank actionBy`() {
            val taskWithBlankActionBy = TestDataBuilder.createTodoTask(actionBy = "   ")
            StepVerifier.create(todoService.save(taskWithBlankActionBy))
                .expectError(BadRequest::class.java)
                .verify()
        }
    }

    @Nested
    @DisplayName("Find Operations")
    internal inner class FindOperations {
        @Test
        @DisplayName("should find existing todo by id")

        internal fun `should find existing todo by id`() {
            val existingTask = TestDataBuilder.createTodoTask(id = "find-test")
            todoRepository.addItem(existingTask)
            StepVerifier.create(todoService.findById("find-test"))
                .expectNext(existingTask)
                .verifyComplete()
        }

        @Test
        @DisplayName("should return empty for non-existent id")

        internal fun `should return empty for non-existent id`() {
            StepVerifier.create(todoService.findById("non-existent"))
                .verifyComplete()
        }

        @Test
        @DisplayName("should handle repository find failure")

        internal fun `should handle repository find failure`() {
            todoRepository.setShouldFailFind(true)
            StepVerifier.create(todoService.findById("any-id"))
                .expectError(RuntimeException::class.java)
                .verify()
        }
    }

    @Nested
    @DisplayName("List Operations")
    internal inner class ListOperations {
        @Test
        @DisplayName("should return empty list when no items exist")

        internal fun `should return empty list when no items exist`() {
            StepVerifier.create(todoService.listItems())
                .expectNext(emptyList())
                .verifyComplete()
        }

        @Test
        @DisplayName("should return all items when they exist")

        internal fun `should return all items when they exist`() {
            val tasks = TestDataBuilder.createTodoList(3)
            tasks.forEach { todoRepository.addItem(it) }
            StepVerifier.create(todoService.listItems())
                .expectNextMatches { list ->
                    list.size == 3 && list.containsAll(tasks)
                }
                .verifyComplete()
        }
    }

    @Nested
    @DisplayName("Update Operations")
    internal inner class UpdateOperations {
        @Test
        @DisplayName("should update existing todo successfully")

        internal fun `should update existing todo successfully`() {
            val originalTask = TestDataBuilder.createTodoTask(id = "update-test")
            todoRepository.addItem(originalTask)
            val updatedTask = TestDataBuilder.createTodoTask(
                id = "update-test",
                description = "Updated description",
                status = "COMPLETED"
            )
            StepVerifier.create(todoService.update("update-test", updatedTask))
                .expectNextMatches { updated ->
                    updated.description == "Updated description" &&
                            updated.status == "COMPLETED"
                }
                .verifyComplete()
        }

        @Test
        @DisplayName("should handle ID mismatch error")

        internal fun `should handle ID mismatch error`() {
            val task = TestDataBuilder.createTodoTask(id = "different-id")
            StepVerifier.create(todoService.update("update-test", task))
                .expectError(RuntimeException::class.java)
                .verify()
        }

        @Test
        @DisplayName("should handle repository update failure")

        internal fun `should handle repository update failure`() {
            val task = TestDataBuilder.createTodoTask(id = "update-test")
            todoRepository.setShouldFailUpdate(true)
            StepVerifier.create(todoService.update("update-test", task))
                .expectError(RuntimeException::class.java)
                .verify()
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    internal inner class DeleteOperations {
        @Test
        @DisplayName("should delete existing todo successfully")

        internal fun `should delete existing todo successfully`() {
            val task = TestDataBuilder.createTodoTask(id = "delete-test")
            todoRepository.addItem(task)
            StepVerifier.create(todoService.delete("delete-test"))
                .expectNext(true)
                .verifyComplete()
            assertThat(todoRepository.size()).isEqualTo(0)
        }

        @Test
        @DisplayName("should return false for non-existent todo")

        internal fun `should return false for non-existent todo`() {
            StepVerifier.create(todoService.delete("non-existent"))
                .expectNext(false)
                .verifyComplete()
        }

        @Test
        @DisplayName("should handle repository delete failure")

        internal fun `should handle repository delete failure`() {
            todoRepository.setShouldFailDelete(true)
            StepVerifier.create(todoService.delete("any-id"))
                .expectError(RuntimeException::class.java)
                .verify()
        }
    }

    @Nested
    @DisplayName("Reactive Behavior")
    internal inner class ReactiveBehavior {
        @Test
        @DisplayName("should handle multiple concurrent operations")

        internal fun `should handle multiple concurrent operations`() {
            val tasks = TestDataBuilder.createTodoList(5)
            val saveMono = tasks.map { todoService.save(it) }
                .reduce { acc, mono -> acc.then(mono) }
            StepVerifier.create(saveMono)
                .expectNextCount(1)
                .verifyComplete()
            StepVerifier.create(todoService.listItems())
                .expectNextMatches { list -> list.size == 5 }
                .verifyComplete()
        }

        @Test
        @DisplayName("should maintain reactive chain on errors")

        internal fun `should maintain reactive chain on errors`() {
            todoRepository.setShouldFailAdd(true)
            val task = TestDataBuilder.createTodoTask()
            StepVerifier.create(
                todoService.save(task)
                    .onErrorReturn(TestDataBuilder.createTodoTask(description = "fallback"))
            )
                .expectNextMatches { it.description == "fallback" }
                .verifyComplete()
        }
    }
} 
