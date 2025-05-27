package com.github.starter.app.todo.endpoints

import com.github.starter.app.todo.model.TodoTask
import com.github.starter.core.testing.FakeTodoService
import com.github.starter.core.testing.TestDataBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@DisplayName("Todo Endpoints")
@ExtendWith(SpringExtension::class)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TodoEndpointsTest(@Autowired val applicationContext: ApplicationContext) {
    private lateinit var todoService: FakeTodoService
    private lateinit var todoEndpoints: TodoEndpoints
    private lateinit var webTestClient: WebTestClient

    @BeforeEach

    internal fun setUp() {
        todoService = FakeTodoService()
        todoEndpoints = TodoEndpoints(todoService)
        webTestClient = WebTestClient
            .bindToController(todoEndpoints)
            .configureClient()
            .codecs { it.defaultCodecs().maxInMemorySize(1024 * 1024) }
            .build()
    }

    @Nested
    @DisplayName("GET /todo/search")
    internal inner class SearchEndpoint {
        @Test
        @DisplayName("should return 200 with valid todo list")

        internal fun `should return 200 with valid todo list`() {
            val todoList = TestDataBuilder.createTodoList(2)
            todoList.forEach { todoService.addTestItem(it) }
            webTestClient.get()
                .uri("/todo/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data").isArray
                .jsonPath("$.data.length()").isEqualTo(todoList.size)
                .jsonPath("$.data[0].id").isEqualTo(todoList[0].id)
                .jsonPath("$.data[0].description").isEqualTo(todoList[0].description)
        }

        @Test
        @DisplayName("should return 200 with empty array when no todos exist")

        internal fun `should return 200 with empty array when no todos exist`() {
            webTestClient.get()
                .uri("/todo/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data").isArray
                .jsonPath("$.data.length()").isEqualTo(0)
        }

        @Test
        @DisplayName("should return 500 when service fails")

        internal fun `should return 500 when service fails`() {
            todoService.setShouldFailListItems(true)
            webTestClient.get()
                .uri("/todo/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError
        }
    }

    @Nested
    @DisplayName("POST /todo/")
    internal inner class CreateEndpoint {
        @Test
        @DisplayName("should create todo and return 201 with location header")

        internal fun `should create todo and return 201 with location header`() {
            val newTodo = TestDataBuilder.createTodoTask()
            webTestClient.post()
                .uri("/todo/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newTodo))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("generated-id")
                .jsonPath("$.data.description").isEqualTo(newTodo.description)
        }

        @Test
        @DisplayName("should return 400 for invalid todo data")

        internal fun `should return 400 for invalid todo data`() {
            val invalidJson = """{"description": ""}"""
            webTestClient.post()
                .uri("/todo/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidJson))
                .exchange()
                .expectStatus().isBadRequest
        }
    }

    @Nested
    @DisplayName("GET /todo/{id}")
    internal inner class GetByIdEndpoint {
        @Test
        @DisplayName("should return todo by id")

        internal fun `should return todo by id`() {
            val todo = TestDataBuilder.createTodoTask(id = "123")
            todoService.addTestItem(todo)
            webTestClient.get()
                .uri("/todo/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("123")
                .jsonPath("$.data.description").isEqualTo(todo.description)
        }
    }
}
