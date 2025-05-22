package com.github.starter.core.secrets

import com.github.skhatri.mounted.MountedSecretsFactory
import com.github.skhatri.mounted.SecretResolver
import com.github.skhatri.mounted.model.SecretConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class SecretsConfigTest {

    @Mock
    private lateinit var mockSecretsProperties: SecretsProperties

    @Mock
    private lateinit var mockSecretConfiguration: SecretConfiguration

    @Mock
    private lateinit var mockSecretResolver: SecretResolver

    private val secretsConfig = SecretsConfig()

    @Test
    fun `createSecretClient returns a SecretsClient instance`() {
        // Arrange
        whenever(mockSecretsProperties.config).thenReturn(mockSecretConfiguration)

        // Mocking the MountedSecretsFactory chain is a bit complex due to new MountedSecretsFactory(...).create()
        // We need to mock the static factory method or the constructor if possible,
        // or ensure that create() on the instance returns our mockSecretResolver.
        // For this test, we'll assume MountedSecretsFactory can be instantiated and its create() can be influenced
        // by controlling its input (mockSecretConfiguration) or by mocking the factory itself if it were injectable.
        // A more direct approach if MountedSecretsFactory was problematic to mock directly would be to
        // use a static mock for MountedSecretsFactory if its methods were static.
        // Here, it's `new MountedSecretsFactory(config).create()`.

        // Simplest unit test: ensure the method doesn't crash and returns a SecretsClient.
        // Deeper mocking of MountedSecretsFactory is possible but might be too involved for this scope.
        // Let's use a static mock for MountedSecretsFactory to control the outcome of `create()` and `noOpResolver()`.
        
        Mockito.mockStatic(MountedSecretsFactory::class.java).use { mockedFactory ->
            val mockMountedSecretsFactoryInstance = Mockito.mock(MountedSecretsFactory::class.java)
            
            // When a new MountedSecretsFactory is instantiated with our mockSecretConfiguration, return our mock instance
            mockedFactory.`when`<Any> { MountedSecretsFactory(mockSecretConfiguration) }.thenReturn(mockMountedSecretsFactoryInstance)
            // When create() is called on that mock instance, return our mockSecretResolver
            whenever(mockMountedSecretsFactoryInstance.create()).thenReturn(mockSecretResolver)

            // Act
            val client = secretsConfig.createSecretClient(mockSecretsProperties)

            // Assert
            assertNotNull(client)
        }
    }

    @Test
    fun `createNoOpSecretClient returns a SecretsClient instance`() {
        // Arrange
        Mockito.mockStatic(MountedSecretsFactory::class.java).use { mockedFactory ->
            mockedFactory.`when`<Any> { MountedSecretsFactory.noOpResolver() }.thenReturn(mockSecretResolver)

            // Act
            val client = secretsConfig.createNoOpSecretClient()

            // Assert
            assertNotNull(client)
        }
    }
}
