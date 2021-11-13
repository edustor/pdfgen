package ru.edustor.pdfgen.internal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

internal class EdustorIdTest {
    @Test
    fun testToString() {
        val id = EdustorId.generate()
        val str = id.toString()
        assertTrue(str.length == 18)
        assertTrue(!str.contains("="))

        val snowflake = Snowflake()
        val (timestampEpoch, nodeId, sequence) = snowflake.parse(str.toLong())

        val timeDelta = Duration.between(Instant.ofEpochMilli(timestampEpoch), Instant.now())
        assertFalse(timeDelta.isNegative)
        assertTrue(timeDelta.toMillis() < 5000)

        assertEquals(sequence, 0)
    }
}