package com.github.starter.app.todo.repository

import com.github.starter.app.todo.endpoints.Todos
import com.github.starter.app.todo.model.TodoTask
import org.junit.jupiter.api.Assertions.*
import java.time.Duration
import java.time.LocalDateTime

class TodoRepositoryUseCases(private val todoRepository: TodoRepository) {
    fun testListTodoTasks() {
        val tasks = requireNotNull(todoRepository.listItems().block()) {
            "Repository should return non-null result"
        }
        assertFalse(tasks.isEmpty()) { "Todo table should have some data" }
    }

    fun verifyAddTodoTask() {
        val task = Todos.create()
        val savedTask = requireNotNull(todoRepository.add(task).block()) {
            "Add operation should return saved task"
        }
        assertEquals(task.description, savedTask.description) {
            "Saved task description should match original"
        }

        val taskId = savedTask.id
        val storedTask = requireNotNull(todoRepository.findById(taskId).block()) {
            "Should be able to retrieve saved task"
        }
        assertEquals(savedTask.description, storedTask.description) {
            "Retrieved task description should match saved task"
        }
    }

    fun verifyDeleteTodoTask() {
        val task = Todos.create()
        val savedTask = requireNotNull(todoRepository.add(task).block()) {
            "Task should be saved successfully"
        }
        assertEquals(task.description, savedTask.description)
        val taskId = savedTask.id
        val deleteResult = requireNotNull(todoRepository.delete(taskId).block()) {
            "Delete operation should return result"
        }
        assertTrue(deleteResult) { "Delete operation should succeed" }

        val secondDeleteResult = todoRepository.delete(taskId)
            .onErrorReturn(false)
            .block() ?: false
        assertFalse(secondDeleteResult) { "Second delete attempt should fail" }
    }

    fun verifyUpdateTodoTask() {
        val task = Todos.create()
        val savedTask = requireNotNull(todoRepository.add(task).block()) {
            "Task should be saved successfully"
        }
        assertEquals(task.description, savedTask.description)
        val taskId = savedTask.id
        val updatedTask = TodoTask(
            id = taskId,
            description = savedTask.description,
            actionBy = "user1",
            created = LocalDateTime.now(),
            status = "DONE",
            updated = LocalDateTime.now()
        )
        val updateResult = todoRepository.update(updatedTask)
            .delayElement(Duration.ofSeconds(1))
            .block()
        requireNotNull(updateResult) { "Update operation should succeed" }

        val retrievedTask = requireNotNull(todoRepository.findById(taskId).block()) {
            "Should be able to retrieve updated task"
        }
        assertEquals("DONE", retrievedTask.status) {
            "Task status should be updated to DONE"
        }
    }
}
