/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

import java.nio.ByteBuffer

data class PublishPacket(
    val op: Int,
    val body: ByteArray,
    val channel: String
) {
    fun toByteArray(): ByteArray {
        val totalLength = 4 + 4 + body.size + 4 + channel.toByteArray().size
        val buffer = ByteBuffer.allocate(totalLength).apply {
            putInt(op)
            putInt(channel.toByteArray().size)
            put(channel.toByteArray())
            putInt(body.size)
            put(body)
        }
        return buffer.array()
    }

    companion object {
        fun fromByteArray(buffer: ByteBuffer): PublishPacket {
            val op = buffer.int
            val channelSize = buffer.int
            val channelBytes = ByteArray(channelSize).apply {
                buffer.get(this)
            }
            val channel = String(channelBytes)
            val bodySize = buffer.int
            val body = ByteArray(bodySize).apply {
                buffer.get(this)
            }
            return PublishPacket(op, body, channel)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublishPacket

        if (op != other.op) return false
        if (!body.contentEquals(other.body)) return false
        if (channel != other.channel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = op
        result = 31 * result + body.contentHashCode()
        result = 31 * result + channel.hashCode()
        return result
    }
}