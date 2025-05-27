package com.github.starter.app.config

import com.github.skhatri.mounted.MountedSecretsFactory
import com.github.starter.core.secrets.SecretsClient
import com.github.starter.core.exception.ConfigurationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestableSecretsClient {
    private val secrets = mutableMapOf<String, CharArray>()
    private val secretsClient = SecretsClient(MountedSecretsFactory.noOpResolver())

    fun addSecret(key: String, value: String) {
        secrets[key] = value.toCharArray()
    }

    fun clear() {
        secrets.clear()
    }

    fun resolve(key: String): CharArray {
        return secrets[key] ?: key.toCharArray()
    }
}

@DisplayName("JDBC Client Preparator Tests")
class JdbcClientPreparatorTest {
    @Test
    @DisplayName("should throw ConfigurationException when no JDBC clients are configured")

    fun `should throw ConfigurationException when no JDBC clients are configured`() {
        val emptyConfig = emptyMap<String, ConfigItem>()
        val secretsClient = SecretsClient(MountedSecretsFactory.noOpResolver())
        val preparator = JdbcClientPreparator(emptyConfig, secretsClient)
        val exception = assertThrows(ConfigurationException::class.java) {
            preparator.configure { _, _ -> }
        }
        assertTrue(exception.message?.contains("no jdbc clients are set") == true) {
            "Exception should mention no jdbc clients configured"
        }
    }

    @Test
    @DisplayName("should handle configuration with disabled JDBC client")

    fun `should handle configuration with disabled JDBC client`() {
        val configItem = ConfigItem().apply {
            name = "disabled-client"
            enabled = false
            driver = "h2"
        }

        val config = mapOf("disabled-client" to configItem)
        val secretsClient = SecretsClient(MountedSecretsFactory.noOpResolver())
        val preparator = JdbcClientPreparator(config, secretsClient)
        val exception = assertThrows(ConfigurationException::class.java) {
            preparator.configure { _, _ -> }
        }
        assertTrue(exception.message?.contains("no jdbc clients are set") == true) {
            "Should throw exception when all clients are disabled"
        }
    }
}
