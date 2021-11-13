package ru.edustor.pdfgen.internal

class EdustorId(private val id: String) {
    companion object {
        private val snowflake = Snowflake()
        fun generate(): EdustorId {
            return EdustorId(snowflake.nextId().toString())
        }
    }

    override fun toString(): String {
        return id
    }
}