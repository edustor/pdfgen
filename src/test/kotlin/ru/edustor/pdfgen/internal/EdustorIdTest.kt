package ru.edustor.pdfgen.internal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EdustorIdTest {
    @Test
    fun testToString() {
        val id = EdustorId.generate(100)
        val str = id.toString()
        assertTrue(str.length == 28)
        assertTrue(!str.contains("="))
    }
}