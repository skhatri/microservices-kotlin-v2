package com.github.starter.core.secrets

import com.github.skhatri.mounted.MountedSecretsFactory
import com.github.skhatri.mounted.MountedSecretsResolver
import com.github.skhatri.mounted.model.SecretConfiguration
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
@Disabled("Mocking issues with MountedSecretsFactory")
class SecretsConfigTest {
    @Mock
    private lateinit var mockSecretsProperties: SecretsProperties

    @Mock
    private lateinit var mockSecretConfiguration: SecretConfiguration
    private val secretsConfig = SecretsConfig()

    @Test

    fun `createSecretClient returns a SecretsClient instance`() {
        whenever(mockSecretsProperties.config).thenReturn(mockSecretConfiguration)
        val client = secretsConfig.createSecretClient(mockSecretsProperties)
        assertNotNull(client)
    }

    @Test

    fun `createNoOpSecretClient returns a SecretsClient instance`() {
        val client = secretsConfig.createNoOpSecretClient()
        assertNotNull(client)
    }
}
