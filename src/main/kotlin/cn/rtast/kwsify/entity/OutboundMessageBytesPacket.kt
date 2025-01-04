/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/3
 */

@file:JvmName("OutboundMessageBytesPacket")

package cn.rtast.kwsify.entity

import java.nio.ByteBuffer
import java.util.*

data class OutboundMessageBytesPacket(
    val op: Int,
    val body: ByteArray,
    val channel: String,
    val sender: Sender
) {
    data class Sender(
        val host: String,
        val port: Int,
        val address: String,
        val uuid: UUID
    )


    fun toByteArray(): ByteArray {
        val hostSize = sender.host.toByteArray().size
        val addressSize = sender.address.toByteArray().size
        val channelSize = channel.toByteArray().size
        val totalLength = body.size + channelSize + hostSize + addressSize + 8 + 8 + 4 + 4 + 4 + 4 + 8
        val buffer = ByteBuffer.allocate(totalLength)
        buffer.putInt(op) // op: 4 bytes
        buffer.putInt(channelSize) // channel size: 4 bytes
        buffer.put(channel.toByteArray()) // channel data
        buffer.putInt(body.size) // body size: 4 bytes
        buffer.put(body) // body data
        buffer.putInt(hostSize) // host size: 4 bytes
        buffer.put(sender.host.toByteArray()) // host data
        buffer.putInt(sender.port) // port: 4 bytes
        buffer.putInt(addressSize) // address size: 4 bytes
        buffer.put(sender.address.toByteArray()) // address data
        buffer.putLong(sender.uuid.mostSignificantBits) // UUID most significant bits: 8 bytes
        buffer.putLong(sender.uuid.leastSignificantBits) // UUID least significant bits: 8 bytes
        return buffer.array()
    }

    companion object {
        fun fromByteArray(buffer: ByteBuffer): OutboundMessageBytesPacket {
            val op = buffer.getInt()
            val channelSize = buffer.getInt()
            val channelBytes = ByteArray(channelSize)
            buffer.get(channelBytes)
            val channel = String(channelBytes)
            val bodySize = buffer.getInt()
            val body = ByteArray(bodySize)
            buffer.get(body)
            val hostSize = buffer.getInt()
            val hostBytes = ByteArray(hostSize)
            buffer.get(hostBytes)
            val host = String(hostBytes)
            val port = buffer.getInt()
            val addressSize = buffer.getInt()
            val addressBytes = ByteArray(addressSize)
            buffer.get(addressBytes)
            val address = String(addressBytes)
            val mostSignificantBits = buffer.getLong()
            val leastSignificantBits = buffer.getLong()
            val uuid = UUID(mostSignificantBits, leastSignificantBits)
            val sender = Sender(host, port, address, uuid)
            return OutboundMessageBytesPacket(op, body, channel, sender)
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OutboundMessageBytesPacket
        return op == other.op &&
                body.contentEquals(other.body) &&
                channel == other.channel &&
                sender == other.sender
    }

    override fun hashCode(): Int {
        var result = op
        result = 31 * result + body.contentHashCode()
        result = 31 * result + channel.hashCode()
        result = 31 * result + (sender?.hashCode() ?: 0)
        return result
    }
}
