package ru.edustor.pdfgen.internal

import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

private val random = Random()

class EdustorId(private val id: String) {
    companion object {
        fun generate(index: Int): EdustorId {
            val bytes = ByteArray(21)
            random.nextBytes(bytes)
            ByteBuffer.wrap(bytes)
                    .position(16)
                    .putInt(Instant.now().epochSecond.toInt())
                    .put(index.toByte())
            val id = Base64.getUrlEncoder().encodeToString(bytes)
            return EdustorId(id)
        }
    }

    override fun toString(): String {
        return id
    }
}