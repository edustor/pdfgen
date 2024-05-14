package ru.edustor.pdfgen.internal

class EdustorId(
    val humanReadableId: String,
    val compactId: String,
) {
    companion object {
        private val snowflake = Snowflake()

        fun new(batchId: String, pageIndex: Int): EdustorId {
            val pageIndexStr = pageIndex.toString().padStart(2, '0')
            val stringBuilder = StringBuilder()
            for (i in 0..batchId.lastIndex step 4) {
                val substring = batchId.substring(i, i + 4)
                stringBuilder.append(substring).append(" ")
            }
            stringBuilder.append(pageIndexStr)
            return EdustorId(
                humanReadableId = stringBuilder.toString(),
                compactId = batchId + pageIndexStr
            )
        }

        fun generateBatchId(): String {
            val snowflakeId = snowflake.nextId()
            return SnowflakeToCharStringEncoder.encode(snowflakeId)
        }
    }

    override fun toString(): String {
        return humanReadableId
    }
}