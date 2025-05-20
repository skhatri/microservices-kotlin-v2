package com.github.starter.core.container

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class MessageItemTest {

    @Test
    fun `primary constructor initializes fields correctly`() {
        val details = mapOf("key" to "value")
        val item = MessageItem("CODE01", "Message 1", details)
        assertEquals("CODE01", item.code)
        assertEquals("Message 1", item.message)
        assertEquals(details, item.details)
    }

    @Test
    fun `primary constructor with empty details`() {
        val item = MessageItem("CODE02", "Message 2", emptyMap())
        assertEquals("CODE02", item.code)
        assertEquals("Message 2", item.message)
        assertTrue(item.details.isEmpty())
    }

    @Test
    fun `secondary constructor initializes with empty details`() {
        val item = MessageItem("CODE03", "Message 3")
        assertEquals("CODE03", item.code)
        assertEquals("Message 3", item.message)
        assertTrue(item.details.isEmpty())
    }

    @Test
    fun `builder creates MessageItem correctly`() {
        val detailsMap = mapOf("info" to "some info")
        val item = MessageItem.Builder()
            .withCode("BUILD_CODE")
            .withMessage("Built Message")
            .withDetails(detailsMap)
            .withDetailItem("anotherKey", "anotherValue")
            .build()

        assertEquals("BUILD_CODE", item.code)
        assertEquals("Built Message", item.message)
        assertEquals(2, item.details.size)
        assertEquals("some info", item.details["info"])
        assertEquals("anotherValue", item.details["anotherKey"])
    }
    
    @Test
    fun `builder withDetailItem adds to existing details`() {
        val initialDetails = mapOf("key1" to "val1")
        val item = MessageItem.Builder()
            .withCode("CODE")
            .withMessage("MSG")
            .withDetails(initialDetails)
            .withDetailItem("key2", "val2")
            .build()
        
        assertEquals(2, item.details.size)
        assertEquals("val1", item.details["key1"])
        assertEquals("val2", item.details["key2"])
    }


    @Test
    fun `builder throws NPE if code is not set before build`() {
        val builder = MessageItem.Builder().withMessage("No Code Message")
        assertThrows<NullPointerException> {
            builder.build()
        }
    }

    @Test
    fun `builder throws NPE if message is not set before build`() {
        val builder = MessageItem.Builder().withCode("No Message Code")
        assertThrows<NullPointerException> {
            builder.build()
        }
    }

    @Test
    fun `equals and hashCode are reference-based`() {
        // MessageItem is not a data class
        val item1 = MessageItem("CODE", "Message", mapOf("d" to "v"))
        val item2 = MessageItem("CODE", "Message", mapOf("d" to "v"))
        val item3 = item1

        assertNotEquals(item1, item2, "Two separate instances with same content should not be equal.")
        // Hashcodes might coincidentally be same, so not a strong test for non-data classes.
        // assertNotEquals(item1.hashCode(), item2.hashCode()) 

        assertEquals(item1, item3, "Same instance should be equal to itself.")
        assertEquals(item1.hashCode(), item3.hashCode())
    }
    
    @Test
    fun `toString returns a non-empty string`() {
        val item = MessageItem("CODE", "Message")
        val str = item.toString()
        // Default toString for non-data classes usually contains FQCN@hashcode
        // We can check for the class name part.
        assertTrue(str.contains("com.github.starter.core.container.MessageItem"))
        // Cannot reliably check for "code=CODE" or "message=Message" as it's not a data class.
    }
}
