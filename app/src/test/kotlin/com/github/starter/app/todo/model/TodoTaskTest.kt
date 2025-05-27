package com.github.starter.app.todo.model

import com.github.starter.core.testing.KotlinTestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions.assertThat

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Todo Task Model")
internal class TodoTaskTest : KotlinTestBase() {
    @Nested
    @DisplayName("Construction and Basic Properties")
    internal inner class ConstructionAndBasicProperties {
        @Test
        @DisplayName("should create valid TodoTask with all required fields")

        internal fun shouldCreateValidTodoTaskWithAllRequiredFields() {
            val id = "task-123"
            val description = "Test task description"
            val actionBy = "test-user"
            val created = LocalDateTime.now()
            val status = "PENDING"
            val todoTask = TodoTask(id, description, actionBy, created, status, null)
            assertThat(todoTask.id, id, "ID should match")
            assertThat(todoTask.description, description, "Description should match")
            assertThat(todoTask.actionBy, actionBy, "ActionBy should match")
            assertThat(todoTask.created, created, "Created timestamp should match")
            assertThat(todoTask.status, status, "Status should match")
            assertThat(todoTask.updated == null, "Updated should be null for new task")
        }

        @Test
        @DisplayName("should handle completed task with updated timestamp")

        internal fun shouldHandleCompletedTaskWithUpdatedTimestamp() {
            val created = LocalDateTime.now().minusHours(1)
            val updated = LocalDateTime.now()
            val todoTask = TodoTask(
                id = "completed-task",
                description = "Completed task",
                actionBy = "user",
                created = created,
                status = "COMPLETED",
                updated = updated
            )
            assertThat(todoTask.status, "COMPLETED", "Status should be COMPLETED")
            assertThat(todoTask.updated, updated, "Updated timestamp should match")
            assertThat(todoTask.updated!!.isAfter(todoTask.created), "Updated should be after created")
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    internal inner class EdgeCasesAndValidation {
        @Test
        @DisplayName("should handle empty description gracefully")

        internal fun shouldHandleEmptyDescriptionGracefully() {
            val todoTask = TodoTask(
                id = "test-id",
                description = "",
                actionBy = "user",
                created = LocalDateTime.now(),
                status = "PENDING",
                updated = null
            )
            assertThat(todoTask.description, "", "Description should be preserved as-is")
        }

        @Test
        @DisplayName("should handle whitespace description gracefully")

        internal fun shouldHandleWhitespaceDescriptionGracefully() {
            val todoTask = TodoTask(
                id = "test-id",
                description = " \t\n",
                actionBy = "user",
                created = LocalDateTime.now(),
                status = "PENDING",
                updated = null
            )
            assertThat(todoTask.description, " \t\n", "Description should be preserved as-is")
        }

        @Test
        @DisplayName("should accept PENDING status")

        internal fun shouldAcceptPendingStatus() {
            val todoTask = createTodoTask(status = "PENDING")
            assertThat(todoTask.status, "PENDING", "Status should match input")
        }

        @Test
        @DisplayName("should accept IN_PROGRESS status")

        internal fun shouldAcceptInProgressStatus() {
            val todoTask = createTodoTask(status = "IN_PROGRESS")
            assertThat(todoTask.status, "IN_PROGRESS", "Status should match input")
        }

        @Test
        @DisplayName("should accept COMPLETED status")

        internal fun shouldAcceptCompletedStatus() {
            val todoTask = createTodoTask(status = "COMPLETED")
            assertThat(todoTask.status, "COMPLETED", "Status should match input")
        }

        @Test
        @DisplayName("should accept CANCELLED status")

        internal fun shouldAcceptCancelledStatus() {
            val todoTask = createTodoTask(status = "CANCELLED")
            assertThat(todoTask.status, "CANCELLED", "Status should match input")
        }

        @Test
        @DisplayName("should handle very long descriptions")

        internal fun shouldHandleVeryLongDescriptions() {
            val longDescription = "a".repeat(10000)
            val todoTask = createTodoTask(description = longDescription)
            assertThat(todoTask.description.length, 10000, "Description length should be 10000")
            assertThat(todoTask.description.all { it == 'a' }, "Description should contain only 'a' characters")
        }

        @Test
        @DisplayName("should handle null updated field correctly")

        internal fun shouldHandleNullUpdatedFieldCorrectly() {
            val todoTask = createTodoTask(status = "PENDING")
            assertThat(todoTask.updated == null, "Updated should be null for pending tasks")
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code")
    internal inner class EqualityAndHashCode {
        @Test
        @DisplayName("should be equal when all properties match")

        internal fun shouldBeEqualWhenAllPropertiesMatch() {
            val timestamp = LocalDateTime.now()
            val task1 = TodoTask("1", "desc", "user", timestamp, "PENDING", null)
            val task2 = TodoTask("1", "desc", "user", timestamp, "PENDING", null)
            assertThat(task1 == task2, "Tasks with same properties should be equal")
            assertThat(task1.hashCode(), task2.hashCode(), "Hash codes should be equal")
        }

        @Test
        @DisplayName("should not be equal when IDs differ")

        internal fun shouldNotBeEqualWhenIdsDiffer() {
            val timestamp = LocalDateTime.now()
            val task1 = TodoTask("1", "desc", "user", timestamp, "PENDING", null)
            val task2 = TodoTask("2", "desc", "user", timestamp, "PENDING", null)
            assertThat(task1 != task2, "Tasks with different IDs should not be equal")
        }

        @Test
        @DisplayName("should not be equal when descriptions differ")

        internal fun shouldNotBeEqualWhenDescriptionsDiffer() {
            val timestamp = LocalDateTime.now()
            val task1 = TodoTask("1", "desc1", "user", timestamp, "PENDING", null)
            val task2 = TodoTask("1", "desc2", "user", timestamp, "PENDING", null)
            assertThat(task1 != task2, "Tasks with different descriptions should not be equal")
        }
    }

    @Nested
    @DisplayName("Serialization and Deserialization")
    internal inner class SerializationAndDeserialization {
        private val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        @Test
        @DisplayName("should serialize to and from JSON correctly")

        internal fun shouldSerializeToAndFromJsonCorrectly() {
            val originalTask = createTodoTask()
            val json = objectMapper.writeValueAsString(originalTask)
            val deserializedTask = objectMapper.readValue(json, TodoTask::class.java)
            assertThat(deserializedTask, originalTask, "Deserialized task should equal original")
        }

        @Test
        @DisplayName("should serialize completed task with updated timestamp")

        internal fun shouldSerializeCompletedTaskWithUpdatedTimestamp() {
            val completedTask = TodoTask(
                id = "completed-task",
                description = "Completed task",
                actionBy = "user",
                created = LocalDateTime.now().minusHours(1),
                status = "COMPLETED",
                updated = LocalDateTime.now()
            )
            val json = objectMapper.writeValueAsString(completedTask)
            json.shouldBeValidJsonWith("id", "description", "created", "status", "updated")
            assertThat(
                json.contains("\"action_by\"") || json.contains("\"actionBy\""),
                "JSON should contain action_by or actionBy field"
            )
            assertThat(json.contains("\"status\":\"COMPLETED\""), "JSON should contain COMPLETED status")
            assertThat(json.contains("\"updated\""), "JSON should contain updated field")
        }

        @Test
        @DisplayName("should handle missing updated field in JSON")

        internal fun shouldHandleMissingUpdatedFieldInJson() {
            val jsonWithoutUpdated = """
                {
                    "id": "test-id",
                    "description": "Test description",
                    "action_by": "test-user",
                    "created": "2023-01-01T10:00:00",
                    "status": "PENDING"
                }
            """.trimIndent()
            val deserializedTask = objectMapper.readValue(jsonWithoutUpdated, TodoTask::class.java)
            assertThat(deserializedTask.updated == null, "Updated should be null when missing from JSON")
            assertThat(deserializedTask.status, "PENDING", "Status should be correctly deserialized")
        }
    }

    @Nested
    @DisplayName("Property-based Testing")
    internal inner class PropertyBasedTesting {
        @Test
        @DisplayName("should maintain invariants for all generated tasks")

        internal fun shouldMaintainInvariantsForAllGeneratedTasks() {
            val testData = generateTestData(50)
            forAll(testData) { index ->
                val task = createTodoTask(id = "task-$index", description = "Description $index")
                task.id.isNotEmpty() &&
                        task.description.isNotEmpty() &&
                        task.actionBy?.isNotEmpty() == true &&
                        task.status?.isNotEmpty() == true &&
                        (task.updated == null || task.updated!!.isAfter(task.created) || task.updated == task.created)
            }
        }

        @Test
        @DisplayName("should handle various status transitions correctly")

        internal fun shouldHandleVariousStatusTransitionsCorrectly() {
            val statuses = listOf("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED", "ON_HOLD")
            forAll(statuses) { status ->
                val task = createTodoTask(status = status)
                task.status == status && task.id.isNotEmpty()
            }
        }
    }

    private fun createTodoTask(
        id: String = "default-id",
        description: String = "Default description",
        actionBy: String = "default-user",
        status: String = "PENDING"
    ): TodoTask = TodoTask(
        id = id,
        description = description,
        actionBy = actionBy,
        created = LocalDateTime.now(),
        status = status,
        updated = if (status == "COMPLETED") LocalDateTime.now() else null
    )
} 
