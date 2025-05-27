package com.github.starter.app.todo.endpoints

import com.github.starter.app.todo.model.TodoTask
import java.time.LocalDate
import java.time.LocalDateTime

object Todos {
    fun createOneForToday(): TodoTask = TodoTask(
        id = "test-todo-${System.currentTimeMillis()}",
        description = "Todo task for ${LocalDate.now()}",
        actionBy = "test-user",
        created = LocalDateTime.now(),
        status = "PENDING",
        updated = null
    )

    fun create(
        id: String = "test-todo-${System.currentTimeMillis()}",
        description: String = "Test todo task",
        actionBy: String = "test-user",
        status: String = "PENDING",
        created: LocalDateTime = LocalDateTime.now(),
        updated: LocalDateTime? = null
    ): TodoTask = TodoTask(
        id = id,
        description = description,
        actionBy = actionBy,
        created = created,
        status = status,
        updated = updated
    )

    fun createList(count: Int = 3): List<TodoTask> =
        (1..count).map { index ->
            create(
                id = "test-todo-$index",
                description = "Test todo task $index",
                actionBy = "test-user-$index"
            )
        }

    fun createCompleted(): TodoTask = create(
        status = "COMPLETED",
        updated = LocalDateTime.now()
    )
}
