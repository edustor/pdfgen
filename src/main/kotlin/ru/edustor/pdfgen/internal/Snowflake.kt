package ru.edustor.pdfgen.internal

import org.slf4j.LoggerFactory
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads
import java.lang.Exception
import kotlin.jvm.Synchronized
import java.time.Instant
import java.lang.StringBuilder
import java.net.NetworkInterface
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

/**
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 *
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of Snowflake per node in your distributed system cluster.
 *
 * Based on https://github.com/callicoder/java-snowflake/blob/a166af56e24a01dd91d5787f0b85afd5091559c0/src/main/java/com/callicoder/snowflake/Snowflake.java
 */
class Snowflake {
    val nodeId: Int
    private val customEpoch: Long

    @Volatile
    private var lastTimestamp = -1L

    private val usedRandomIds = ConcurrentHashMap.newKeySet<Int>()

    // Create Snowflake with a nodeId and custom epoch
    // Create Snowflake with a nodeId
    @JvmOverloads
    constructor(nodeId: Int, customEpoch: Long = DEFAULT_CUSTOM_EPOCH) {
        require(!(nodeId < 0 || nodeId > maxNodeId)) { String.format("NodeId must be between %d and %d", 0, maxNodeId) }
        this.nodeId = nodeId
        this.customEpoch = customEpoch
    }

    // Let Snowflake generate a nodeId
    constructor() {
        nodeId = createNodeId()
        customEpoch = DEFAULT_CUSTOM_EPOCH
    }

    @Synchronized
    fun nextId(): Long {
        var currentTimestamp: Long;
        var randomPart: Int;
        while (true) {
            currentTimestamp = timestamp()
            check(currentTimestamp >= lastTimestamp) { "Invalid System Clock!" }

            if (currentTimestamp != lastTimestamp) {
                usedRandomIds.clear()
                lastTimestamp = currentTimestamp
            }

            randomPart = ThreadLocalRandom.current().nextInt(maxRandom)
            if (!usedRandomIds.add(randomPart)) {
                logger.warn("Random part collision occurred, retrying. timestamp={}, usedRandomIds.size={}", currentTimestamp, usedRandomIds.size)
                continue
            }
            break
        }

        return (currentTimestamp.shl(NODE_ID_BITS + RANDOM_BITS)
                or nodeId.toLong().shl(RANDOM_BITS)
                or randomPart.toLong())
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private fun timestamp(): Long {
        return Instant.now().epochSecond - customEpoch
    }

    private fun createNodeId(): Int {
        var nodeId: Int = try {
            val sb = StringBuilder()
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val mac = networkInterface.hardwareAddress
                if (mac != null) {
                    for (macPort in mac) {
                        sb.append(String.format("%02X", macPort))
                    }
                }
            }
            sb.toString().hashCode().toLong().toInt()
        } catch (ex: Exception) {
            SecureRandom().nextInt()
        }
        nodeId = nodeId and maxNodeId
        return nodeId
    }

    fun parse(id: Long): ParsedSnowflakeData {
        val nodeIdMask = (1L shl NODE_ID_BITS + RANDOM_BITS) - (1L shl RANDOM_BITS)
        val randomPartMask = (1L shl RANDOM_BITS) - 1

        val timestamp = id.shr(NODE_ID_BITS + RANDOM_BITS) + customEpoch
        val nodeId = id.and(nodeIdMask).shr(RANDOM_BITS)
        val randomPart = id.and(randomPartMask)

        return ParsedSnowflakeData(Instant.ofEpochSecond(timestamp), nodeId.toInt(), randomPart.toInt())
    }

    override fun toString(): String {
        return ("Snowflake Settings [EPOCH_BITS=" + EPOCH_BITS + ", NODE_ID_BITS=" + NODE_ID_BITS
                + ", RANDOM_BITS=" + RANDOM_BITS + ", CUSTOM_EPOCH=" + customEpoch
                + ", NodeId=" + nodeId + "]")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Snowflake::class.java)

        private const val UNUSED_BITS = 4 // Sign bit, Unused (always set to 0)
        private const val EPOCH_BITS = 35
        private const val NODE_ID_BITS = 7
        private const val RANDOM_BITS = 18
        private const val maxNodeId = (1 shl NODE_ID_BITS) - 1
        private const val maxRandom = (1 shl RANDOM_BITS) - 1

        // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
        private const val DEFAULT_CUSTOM_EPOCH = 1420070400L
    }

    data class ParsedSnowflakeData(val timestamp: Instant, val nodeId: Int, val randomPart: Int)

}