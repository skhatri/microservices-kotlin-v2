package com.github.starter.karate;

object Conditions {
    fun ignoreMock(): Boolean = checkEnv("NO_WIREMOCK")

    private fun checkEnv(envName: String): Boolean {
        return System.getenv(envName)?.toBoolean() ?: false
    }

    fun ignoreBoot(): Boolean = checkEnv("NO_BOOT")
}
