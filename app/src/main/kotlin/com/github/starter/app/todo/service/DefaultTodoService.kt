package com.github.starter.app.todo.service;

import com.github.starter.app.todo.model.TodoTask;
import com.github.starter.app.todo.repository.TodoRepository;
import com.github.starter.core.exception.BadRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
open class DefaultTodoService(private val todoRepository: TodoRepository) : TodoService {
    @Override

    override fun listItems(): Mono<List<TodoTask>> {
        return todoRepository.listItems();
    }

    @Override

    override fun findById(id: String): Mono<TodoTask> {
        return todoRepository.findById(id);
    }

    @Override

    override fun save(task: TodoTask): Mono<TodoTask> {
        if (task.description.isBlank()) {
            return Mono.error(
                BadRequest.Companion.forCodeAndMessage(
                    "invalid-description",
                    "Description cannot be empty or blank"
                )
            )
        }
        if (task.actionBy?.isBlank() != false) {
            return Mono.error(
                BadRequest.Companion.forCodeAndMessage(
                    "invalid-action-by",
                    "ActionBy cannot be empty or blank"
                )
            )
        }
        return todoRepository.add(task);
    }

    @Override

    override fun update(id: String, task: TodoTask): Mono<TodoTask> {
        if (id != task.id) {
            return Mono.error(
                BadRequest.Companion.forCodeAndMessage(
                    "invalid-id",
                    "Provided Task ID does not match one in Payload"
                )
            );
        }
        return todoRepository.update(task);
    }

    @Override

    override fun delete(id: String): Mono<Boolean> {
        return todoRepository.delete(id);
    }
}
