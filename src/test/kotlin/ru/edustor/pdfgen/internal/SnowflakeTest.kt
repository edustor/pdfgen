package ru.edustor.pdfgen.internal;

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class SnowflakeTest {

    @Test
    fun nextId() {
        val snowflake = Snowflake()
        val id = snowflake.nextId()
        assertEquals(0, id shr 60)

        val parsed = snowflake.parse(id)

        val timeDelta = Duration.between(parsed.timestamp, Instant.now())
        assertFalse(timeDelta.isNegative)
        assertTrue(timeDelta.toMillis() < 5000)
        assertTrue(parsed.randomPart > 0)

        assertEquals(snowflake.nodeId, parsed.nodeId)
    }
}