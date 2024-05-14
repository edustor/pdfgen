package ru.edustor.pdfgen.internal

import kotlin.math.pow

object SnowflakeToCharStringEncoder {
    private val chars = arrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', '2', '3', '4', '5', '6', '7'
    )

    private val indexByChar = let {
        var i = 0;
        val result = HashMap<Char, Int>();
        for (c in chars) {
            result[c] = i++
        }
        result
    };

    fun encode(id: Long): String {
        var remaining = id;
        var temp: Int;

        val result = StringBuilder()

        val seedMask = (1L.shl(XOR_SEED_CHARS_LENGTH * 5)) - 1;
        val seed = id.and(seedMask)
        val generator = StableSequenceGenerator(seed)

        for (i in 0 until 12) {
            temp = remaining.mod(32);
            remaining = remaining.shr(5);
            if (i >= XOR_SEED_CHARS_LENGTH) {
                temp = temp.xor(generator.next()).mod(32)
            }
            result.append(chars[temp])
        }

        if (remaining != 0L) {
            throw IllegalArgumentException("Snowflake id must have at most 60 bits, id=$id")
        }

        return result.toString()
    }

    fun decode(encoded: String): Long {
        var result = 0L
        var offset = 0
        var generator: StableSequenceGenerator? = null

        for (c in encoded) {
            var temp = indexByChar[c] ?: throw IllegalArgumentException("Character $c is not found in encoding map")
            if (generator != null) {
                temp = temp.xor(generator.next()).mod(32)
            }
            result = result.or(temp.toLong().shl(5 * offset++))
            if (offset == 4) {
                generator = StableSequenceGenerator(result)
            }
        }
        return result;
    }

    private const val XOR_SEED_CHARS_LENGTH = 4;

    /**
     * Simple linear congruential generator implementation, that can be portable between different languages
     * https://stackoverflow.com/a/3062783
     */
    private class StableSequenceGenerator(private var seed: Long) {
        fun next(): Int {
            seed = (A * seed + C).mod(M)
            return seed.toInt()
        }

        companion object {
            const val A = 25214903917;
            const val C = 11;
            val M = 2.0.pow(48).toLong();
        }
    }
}