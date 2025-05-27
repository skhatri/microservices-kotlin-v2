package com.github.starter.app.todo.repository;

import com.github.skhatri.mounted.MountedSecretsFactory
import com.github.skhatri.mounted.model.ErrorDecision
import com.github.skhatri.mounted.model.SecretConfiguration
import com.github.skhatri.mounted.model.SecretProvider
import com.github.starter.app.config.ConfigItem
import com.github.starter.app.config.JdbcClientConfig
import com.github.starter.app.config.JdbcProperties
import com.github.starter.core.secrets.SecretsClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.time.Duration
import java.util.function.Consumer

@DisplayName("Todo Repository Integration Tests")

@Testcontainers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfSystemProperty(named = "run.integration.tests", matches = "true")
class DefaultTodoRepositoryIntegrationTest {
    private var todoRepositoryUseCases: TodoRepositoryUseCases? = null

    companion object {
        @Container
        @JvmStatic
        val postgres = GenericContainer("postgres:17.0")
            .withEnv("POSTGRES_PASSWORD", "admin").withExposedPorts(5432)
            .withStartupAttempts(2)
            .withCopyFileToContainer(
                MountableFile.forHostPath("../scripts/containers/postgres/csv"), "/tmp/data/csv"
            )
            .withCopyFileToContainer(
                MountableFile.forHostPath("../scripts/containers/postgres/sql/pg.sql"),
                "/docker-entrypoint-initdb.d/test.sql"
            ).waitingFor(
                Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1)
                    .withStartupTimeout(Duration.ofSeconds(60))
            )
            .withLogConsumer(Consumer { c -> print(c.toString()) })

        @BeforeAll
        @JvmStatic

        fun setUpContainer() {
            postgres.start()
            println("PostgreSQL started on port ${postgres.getMappedPort(5432)}")
        }

        @AfterAll
        @JvmStatic

        fun tearDownContainer() {
            postgres.stop()
        }
    }

    @BeforeEach

    fun setUp() {
        val randomPort = postgres.getMappedPort(5432)
        println("port ${randomPort}")
        val defaultJdbcName = "default-jdbc-client"
        val cfg = ConfigItem().apply {
            driver = "postgresql"
            enabled = true
            host = "localhost"
            port = randomPort
            username = "secret::vault::postgres:-postgres"
            password = "secret::vault::postgres:-admin"
            name = defaultJdbcName
            database = "starter"
        }

        val jdbcProperties = JdbcProperties(listOf(cfg))
        val jdbcClientConfig = JdbcClientConfig()
        val config = jdbcClientConfig.databaseProperties(jdbcProperties)
        val secretProvider = SecretProvider().apply {
            entriesLocation = "all.properties"
            errorDecision = ErrorDecision.EMPTY.toString().lowercase()
            isIgnoreResourceFailure = true
            name = "vault"
            mount = "/doesntexist"
        }

        val secretConfiguration = SecretConfiguration()
        secretConfiguration.setKeyErrorDecision(ErrorDecision.IDENTITY.toString().lowercase())
        secretConfiguration.providers = listOf(secretProvider)
        val mountedSecretsResolver = MountedSecretsFactory(secretConfiguration).create()
        val secretsClient = SecretsClient(mountedSecretsResolver)
        val factory = jdbcClientConfig.dataSources(config, secretsClient)
        this.todoRepositoryUseCases = TodoRepositoryUseCases(DefaultTodoRepository(factory, defaultJdbcName))
    }

    @DisplayName("list todo")

    @Test

    fun testListTodo() {
        todoRepositoryUseCases!!.testListTodoTasks()
    }

    @DisplayName("add todo")

    @Test

    fun testAddTodoTask() {
        todoRepositoryUseCases!!.verifyAddTodoTask()
    }

    @DisplayName("delete todo")

    @Test

    fun testDeleteTodoTask() {
        todoRepositoryUseCases!!.verifyDeleteTodoTask()
    }

    @DisplayName("update todo")

    @Test

    fun testUpdateTodoTask() {
        todoRepositoryUseCases!!.verifyUpdateTodoTask()
    }
}
