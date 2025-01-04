/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/3
 */


package cn.rtast.kwsify.entity

import java.nio.ByteBuffer

data class OPCodePacket(
    val op: Int,
    val channel: String,
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(4 + 4 + channel.toByteArray().size)
        return buffer.apply {
            putInt(op)
            putInt(channel.toByteArray().size)
            put(channel.toByteArray())
        }.array()
    }

    companion object {
        fun fromByteArray(buffer: ByteBuffer): OPCodePacket {
            val op = buffer.int
            val channelSize = buffer.int
            val channelBytes = ByteArray(channelSize).apply {
                buffer.get(this)
            }
            val channel = String(channelBytes)
            return OPCodePacket(op, channel)
        }
    }
}