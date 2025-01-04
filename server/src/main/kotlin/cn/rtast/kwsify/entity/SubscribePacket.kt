/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */

@file:JvmName("SubscribePacket")

package cn.rtast.kwsify.entity

import cn.rtast.kwsify.util.getBoolean
import cn.rtast.kwsify.util.putBoolean
import java.nio.ByteBuffer
import java.util.*

data class SubscribePacket(
    val op: Int,
    val uuid: UUID,
    val channel: String,
    val broadcastSelf: Boolean
) {
    fun toByteArray(): ByteArray {
        val totalLength = 4 + 16 + 4 + channel.toByteArray().size + 1
        val buffer = ByteBuffer.allocate(totalLength).apply {
            putInt(op)
            putInt(channel.toByteArray().size)
            put(channel.toByteArray())
            putBoolean(broadcastSelf)
            putLong(uuid.mostSignificantBits)
            putLong(uuid.leastSignificantBits)
        }
        return buffer.array()
    }

    companion object {
        fun fromByteArray(buffer: ByteBuffer): SubscribePacket {
            val op = buffer.getInt()
            val channelSize = buffer.getInt()
            val channelBytes = ByteArray(channelSize)
            buffer.get(channelBytes)
            val channel = String(channelBytes)
            val broadcastSelf = buffer.getBoolean()
            val mostSignificantBits = buffer.getLong()
            val leastSignificantBits = buffer.getLong()
            val uuid = UUID(mostSignificantBits, leastSignificantBits)
            return SubscribePacket(op, uuid, channel, broadcastSelf)
        }
    }
}