package com.github.starter.core.testing

import com.github.starter.app.todo.model.TodoTask
import com.github.starter.app.todo.repository.TodoRepository
import com.github.starter.app.todo.service.TodoService
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

class FakeTodoRepository : TodoRepository {
    private val storage = ConcurrentHashMap<String, TodoTask>()
    private var shouldFailFind = false
    private var shouldFailAdd = false
    private var shouldFailUpdate = false
    private var shouldFailDelete = false

    fun setShouldFailFind(fail: Boolean) {
        shouldFailFind = fail
    }

    fun setShouldFailAdd(fail: Boolean) {
        shouldFailAdd = fail
    }

    fun setShouldFailUpdate(fail: Boolean) {
        shouldFailUpdate = fail
    }

    fun setShouldFailDelete(fail: Boolean) {
        shouldFailDelete = fail
    }

    fun clear() {
        storage.clear()
    }

    fun addItem(item: TodoTask) {
        storage[item.id] = item
    }

    fun size() = storage.size

    override fun listItems(): Mono<List<TodoTask>> {
        return Mono.just(storage.values.toList())
    }

    override fun findById(id: String): Mono<TodoTask> {
        return if (shouldFailFind) {
            Mono.error(RuntimeException("Repository find failed"))
        } else {
            storage[id]?.let { Mono.just(it) } ?: Mono.empty()
        }
    }

    override fun add(item: TodoTask): Mono<TodoTask> {
        return if (shouldFailAdd) {
            Mono.error(RuntimeException("Repository add failed"))
        } else {
            storage[item.id] = item
            Mono.just(item)
        }
    }

    override fun update(item: TodoTask): Mono<TodoTask> {
        return if (shouldFailUpdate) {
            Mono.error(RuntimeException("Repository update failed"))
        } else {
            storage[item.id] = item
            Mono.just(item)
        }
    }

    override fun delete(id: String): Mono<Boolean> {
        return if (shouldFailDelete) {
            Mono.error(RuntimeException("Repository delete failed"))
        } else {
            val removed = storage.remove(id) != null
            Mono.just(removed)
        }
    }
}

class FakeTodoService : TodoService {
    private val repository = FakeTodoRepository()
    private var shouldFailListItems = false
    private var shouldFailSave = false

    fun setShouldFailListItems(fail: Boolean) {
        shouldFailListItems = fail
    }

    fun setShouldFailSave(fail: Boolean) {
        shouldFailSave = fail
    }

    fun addTestItem(item: TodoTask) {
        repository.addItem(item)
    }

    fun clear() {
        repository.clear()
    }

    override fun listItems(): Mono<List<TodoTask>> {
        return if (shouldFailListItems) {
            Mono.error(RuntimeException("Service unavailable"))
        } else {
            repository.listItems()
        }
    }

    override fun findById(id: String): Mono<TodoTask> {
        return repository.findById(id)
    }

    override fun save(item: TodoTask): Mono<TodoTask> {
        return if (shouldFailSave) {
            Mono.error(RuntimeException("Save failed"))
        } else {
            val savedItem = TodoTask(
                id = if (item.id == "test-id") "generated-id" else item.id,
                description = item.description,
                actionBy = item.actionBy,
                created = item.created,
                status = item.status,
                updated = item.updated
            )
            repository.add(savedItem)
        }
    }

    override fun update(id: String, item: TodoTask): Mono<TodoTask> {
        return if (id == item.id) {
            repository.update(item)
        } else {
            Mono.error(RuntimeException("ID mismatch"))
        }
    }

    override fun delete(id: String): Mono<Boolean> {
        return repository.delete(id)
    }
}

object TestDataBuilder {
    fun createTodoTask(
        id: String = "test-id",
        description: String = "Test description",
        actionBy: String = "test-user",
        status: String = "PENDING"
    ): TodoTask = TodoTask(
        id = id,
        description = description,
        actionBy = actionBy,
        created = java.time.LocalDateTime.now(),
        status = status,
        updated = if (status == "COMPLETED") java.time.LocalDateTime.now() else null
    )

    fun createTodoList(size: Int = 2): List<TodoTask> =
        (1..size).map { i ->
            createTodoTask(
                id = i.toString(),
                description = "Task $i",
                status = if (i % 2 == 0) "COMPLETED" else "PENDING"
            )
        }
} 
