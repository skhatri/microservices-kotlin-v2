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
    private lateinit var mockSecretConfiguration: SecretConfiguration

    @Test

    fun `SecretsProperties can be instantiated and config can be set and retrieved`() {
        val properties = SecretsProperties()
        assertNull(properties.config, "Initially, config should be null.")
        properties.config = mockSecretConfiguration
        assertEquals(mockSecretConfiguration, properties.config, "Should be able to set and get the config.")
    }

    @Test

    fun `SecretsProperties constructor can accept a SecretConfiguration`() {
        val propertiesWithConfig = SecretsProperties(config = mockSecretConfiguration)
        assertEquals(mockSecretConfiguration, propertiesWithConfig.config, "Constructor should set the config.")
        val propertiesWithNullConfig = SecretsProperties(config = null)
        assertNull(propertiesWithNullConfig.config, "Constructor should allow setting null config.")
    }
}
