package com.github.starter.core.container

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Base64
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class TokenInformationTest {
    @Test

    fun `encode generates correct Base64 token`() {
        val expectedDecodedString = "15:5"
        val encoded = TokenInformation.encode(start = 10, limit = 5)
        val actualDecodedString = String(Base64.getDecoder().decode(encoded))
        assertEquals(expectedDecodedString, actualDecodedString)
        val expectedDecodedString2 = "10:10"
        val encoded2 = TokenInformation.encode(start = 0, limit = 10)
        val actualDecodedString2 = String(Base64.getDecoder().decode(encoded2))
        assertEquals(expectedDecodedString2, actualDecodedString2)
    }

    @Test

    fun `decode with empty string returns default start and limit`() {
        val (start, limit) = TokenInformation.decode("")
        assertEquals(0, start)
        assertEquals(10, limit)
    }

    @Test

    fun `decode valid token returns correct start and limit based on current logic`() {
        val token = Base64.getEncoder().encodeToString("20:5".toByteArray())
        val (start, limit) = TokenInformation.decode(token)
        assertEquals(20, start)
        assertEquals(5, limit)
        val token2 = Base64.getEncoder().encodeToString("10:10".toByteArray())
        val (start2, limit2) = TokenInformation.decode(token2)
        assertEquals(10, start2)
        assertEquals(10, limit2)
    }

    @Test

    fun `decode behavior reflects that 'start' is the first part of decoded token, not calculated back`() {
        val encodedToken = TokenInformation.encode(start = 10, limit = 5)
        val (decodedStart, decodedLimit) = TokenInformation.decode(encodedToken)
        assertEquals(5, decodedLimit, "Limit should be decoded correctly.")
        assertEquals(15, decodedStart, "Decoded start is the sum (start+limit) from encoding, not original start.")
    }

    @Test

    fun `decode with malformed Base64 string throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            TokenInformation.decode("this-is-not-base64")
        }
    }

    @Test

    fun `decode with valid Base64 but incorrect format (no colon) throws NumberFormatException`() {
        val token = Base64.getEncoder().encodeToString("justaword".toByteArray())
        assertThrows<NumberFormatException> {
            TokenInformation.decode(token)
        }
    }

    @Test

    fun `decode with valid Base64 but non-integer parts throws NumberFormatException`() {
        val token = Base64.getEncoder().encodeToString("abc:def".toByteArray())
        assertThrows<NumberFormatException> {
            TokenInformation.decode(token)
        }
    }

    @Test

    fun `createPageMeta returns map with nextToken if recordCount equals limit`() {
        val recordCount = 10
        val start = 0
        val limit = 10
        val pageMeta = TokenInformation.createPageMeta(recordCount, start, limit)
        assertTrue(pageMeta.containsKey("nextToken"))
        assertNotNull(pageMeta["nextToken"])
        assertEquals(1, pageMeta.size)
        val expectedNextTokenPayload = "20:10"
        val expectedNextToken = Base64.getEncoder().encodeToString(expectedNextTokenPayload.toByteArray())
        assertEquals(expectedNextToken, pageMeta["nextToken"])
    }

    @Test

    fun `createPageMeta returns empty map if recordCount is less than limit`() {
        val recordCount = 5
        val start = 0
        val limit = 10
        val pageMeta = TokenInformation.createPageMeta(recordCount, start, limit)
        assertTrue(pageMeta.isEmpty())
    }

    @Test

    fun `createPageMeta returns empty map if recordCount is greater than limit`() {
        val recordCount = 15
        val start = 0
        val limit = 10
        val pageMeta = TokenInformation.createPageMeta(recordCount, start, limit)
        assertTrue(pageMeta.isEmpty())
    }
}
