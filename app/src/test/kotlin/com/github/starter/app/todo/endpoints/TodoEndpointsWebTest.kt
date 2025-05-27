package com.github.starter.app.todo.endpoints

import com.github.starter.app.todo.model.TodoTask
import com.github.starter.app.todo.service.TodoService
import com.github.starter.core.container.Container
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import org.junit.jupiter.api.Disabled

@ExtendWith(MockitoExtension::class)
class TodoEndpointsWebTest {
    @Mock
    private lateinit var todoService: TodoService
    private lateinit var webTestClient: WebTestClient
    private lateinit var todoEndpoints: TodoEndpoints

    @BeforeEach

    fun setup() {
        todoEndpoints = TodoEndpoints(todoService)
        webTestClient = WebTestClient
            .bindToController(todoEndpoints)
            .configureClient()
            .codecs { configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true) }
            .build()
    }

    @Test

    fun `should return todo list`() {
        val todoList = listOf(
            TodoTask("1", "Task 1", "User", LocalDateTime.now(), "PENDING", null),
            TodoTask("2", "Task 2", "User", LocalDateTime.now(), "COMPLETED", null)
        )
        `when`(todoService.listItems()).thenReturn(Mono.just(todoList))
        webTestClient.get()
            .uri("/todo/search")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.data").isArray()
            .jsonPath("$.data.length()").isEqualTo(2)
            .jsonPath("$.data[0].id").isEqualTo("1")
            .jsonPath("$.data[1].id").isEqualTo("2")
    }

    @Test

    fun `should return single todo by id`() {
        val todoTask = TodoTask("1", "Task 1", "User", LocalDateTime.now(), "PENDING", null)
        `when`(todoService.findById("1")).thenReturn(Mono.just(todoTask))
        webTestClient.get()
            .uri("/todo/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.data.id").isEqualTo("1")
            .jsonPath("$.data.description").isEqualTo("Task 1")
    }

    @Test
    @Disabled("Needs fixing")

    fun `should add new todo task`() {
        val newTask = TodoTask("3", "New Task", "User", LocalDateTime.now(), "PENDING", null)
        `when`(todoService.save(newTask)).thenReturn(Mono.just(newTask))
        webTestClient.post()
            .uri("/todo/")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(newTask))
            .exchange()
            .expectBody(String::class.java)
            .consumeWith { result ->
                println("Response body: ${result.responseBody}")
            }
    }

    @Test
    @Disabled("Needs fixing")

    fun `should update existing todo task`() {
        val updatedTask = TodoTask("4", "Updated Task", "User", LocalDateTime.now(), "COMPLETED", LocalDateTime.now())
        `when`(todoService.update("4", updatedTask)).thenReturn(Mono.just(updatedTask))
        webTestClient.post()
            .uri("/todo/4")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updatedTask))
            .exchange()
            .expectBody(String::class.java)
            .consumeWith { result ->
                println("Response body: ${result.responseBody}")
            }
    }

    @Test

    fun `should delete todo task`() {
        `when`(todoService.delete("5")).thenReturn(Mono.just(true))
        webTestClient.delete()
            .uri("/todo/5")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.data.result").isEqualTo(true)
    }
} 
