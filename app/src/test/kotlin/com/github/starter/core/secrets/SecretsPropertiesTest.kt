package com.github.starter.core.secrets

import com.github.skhatri.mounted.model.SecretConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class SecretsPropertiesTest {

    @Mock
    private lateinit var mockSecretConfiguration: SecretConfiguration // Mocking the external type

    @Test
    fun `SecretsProperties can be instantiated and config can be set and retrieved`() {
        val properties = SecretsProperties() // Test default instantiation

        // Initially, config should be null
        assertNull(properties.config, "Initially, config should be null.")

        // Set a mocked SecretConfiguration
        properties.config = mockSecretConfiguration
        assertEquals(mockSecretConfiguration, properties.config, "Should be able to set and get the config.")
    }

    @Test
    fun `SecretsProperties constructor can accept a SecretConfiguration`() {
        // Test the constructor that Spring would use for property binding (if it exists and is used that way)
        // The class definition is `class SecretsProperties(var config: SecretConfiguration? = null)`
        // This means there's a primary constructor that can take SecretConfiguration.
        
        val propertiesWithConfig = SecretsProperties(config = mockSecretConfiguration)
        assertEquals(mockSecretConfiguration, propertiesWithConfig.config, "Constructor should set the config.")

        val propertiesWithNullConfig = SecretsProperties(config = null)
        assertNull(propertiesWithNullConfig.config, "Constructor should allow setting null config.")
    }
}
