/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

import java.nio.ByteBuffer

data class UnsubscribePacket(
    val op: Int,
    val channel: String,
) {

    fun toByteArray(): ByteArray {
        val totalLength = 4 + 4 + channel.toByteArray().size
        val buffer = ByteBuffer.allocate(totalLength).apply {
            putInt(op)
            putInt(channel.toByteArray().size)
            put(channel.toByteArray())
        }
        return buffer.array()
    }

    companion object {}
}