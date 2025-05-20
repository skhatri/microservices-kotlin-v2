package com.github.starter.core.model

import org.junit.jupiter.api.Test
import kotlin.test.*

class Tuple2Test {

    @Test
    fun `constructor initializes properties correctly`() {
        val tuple1 = Tuple2("Hello", 123)
        assertEquals("Hello", tuple1.first)
        assertEquals(123, tuple1.second)

        val listData = listOf("a", "b")
        val tuple2 = Tuple2(true, listData)
        assertEquals(true, tuple2.first)
        assertSame(listData, tuple2.second) // Check for same instance for collections/objects

        val tuple3 = Tuple2<String?, Int?>(null, null)
        assertNull(tuple3.first)
        assertNull(tuple3.second)
        
        val tuple4 = Tuple2<String?, Int?>("Not Null", null)
        assertEquals("Not Null", tuple4.first)
        assertNull(tuple4.second)
    }

    @Test
    fun `equals and hashCode are reference-based`() {
        val tuple1a = Tuple2("A", 1)
        val tuple1b = Tuple2("A", 1) // Same content, different instance
        val tuple2 = Tuple2("B", 2)
        val tuple1c = tuple1a // Same instance

        assertNotEquals(tuple1a, tuple1b, "Different instances with same content should not be equal.")
        assertNotEquals(tuple1a.hashCode(), tuple1b.hashCode(), "HashCodes for different instances might differ (though not guaranteed for all cases, usually do).")
        
        assertNotEquals(tuple1a, tuple2)

        assertEquals(tuple1a, tuple1c, "Same instance should be equal to itself.")
        assertEquals(tuple1a.hashCode(), tuple1c.hashCode())
    }

    @Test
    fun `toString returns a string containing the class name`() {
        val tuple = Tuple2("X", 100)
        val tupleToString = tuple.toString()
        assertTrue(tupleToString.contains("com.github.starter.core.model.Tuple2"))
        // Cannot reliably check for "first=X" or "second=100" as it's not a data class
    }
    
    @Test
    fun `can hold different types including nullable ones`() {
        val t1 = Tuple2<String, Int>("text", 42)
        assertEquals("text", t1.first)
        assertEquals(42, t1.second)

        val t2 = Tuple2<Double?, List<String>?>(null, listOf("a", "b", "c"))
        assertNull(t2.first)
        assertEquals(3, t2.second?.size)

        val t3 = Tuple2<Boolean, Unit?>(true, null)
        assertTrue(t3.first)
        assertNull(t3.second)
    }
}
