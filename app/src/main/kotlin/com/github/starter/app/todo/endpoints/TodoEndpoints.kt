package com.github.starter.app.todo.endpoints;

import com.github.starter.app.todo.model.TodoTask;
import com.github.starter.app.todo.service.TodoService;
import com.github.starter.core.container.Container;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono;
import java.net.URI;

@RestController
@RequestMapping("/todo")
@CrossOrigin(origins = ["*"])
class TodoEndpoints(private val todoService: TodoService) {
    @GetMapping("/search")

    fun list(): Mono<Container<List<TodoTask>>> {
        return todoService.listItems().map { Container(it) }
    }

    @GetMapping("/{id}")

    fun get(@PathVariable("id") id: String): Mono<Container<TodoTask>> {
        return todoService.findById(id).map { Container<TodoTask>(it) }
    }

    @PostMapping(value = ["/"], consumes = [MediaType.APPLICATION_JSON_VALUE])

    fun add(@RequestBody todoTask: TodoTask): Mono<ResponseEntity<Container<TodoTask>>> {
        return todoService.save(todoTask).map { savedTask ->
            ResponseEntity.created(URI.create("/todo/${savedTask.id}"))
                .body(Container(savedTask))
        }
    }

    @PostMapping(value = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])

    fun update(@PathVariable("id") id: String, @RequestBody todoTask: TodoTask): Mono<Container<TodoTask>> {
        return todoService.update(id, todoTask).map { Container(it) }
    }

    @DeleteMapping("/{id}")

    fun delete(@PathVariable("id") id: String): Mono<Container<Map<String, Boolean>>> {
        return todoService.delete(id).map { b -> mapOf("result" to b) }.map { Container(it) };
    }
}
