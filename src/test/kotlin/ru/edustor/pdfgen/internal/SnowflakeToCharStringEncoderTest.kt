package ru.edustor.pdfgen.internal;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions.*;

class SnowflakeToCharStringEncoderTest {

    @Test
    fun encode() {
        val id = 373996990697959488
        val result = SnowflakeToCharStringEncoder.encode(id)
        assertEquals("AC43SS5NL35C", result) // Non-XORed: AC43ZIAJUFMK
    }

    @Test
    fun decode() {
        val id = SnowflakeToCharStringEncoder.decode("AC43SS5NL35C")
        assertEquals(373996990697959488, id)
    }

    @Test
    fun encodeSnowflake() {
        val snowflake = Snowflake()
        for (i in 0 until 64) {
            val id = snowflake.nextId()
            val result = SnowflakeToCharStringEncoder.encode(id)
            assertEquals(12, result.length)
            println(result)
        }
    }
}