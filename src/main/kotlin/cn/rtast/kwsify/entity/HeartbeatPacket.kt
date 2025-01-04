/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/4
 */


package cn.rtast.kwsify.entity

import java.nio.ByteBuffer

data class HeartbeatPacket(val op: Int) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(4).apply {
            putInt(op)
        }
        return buffer.array()
    }
}