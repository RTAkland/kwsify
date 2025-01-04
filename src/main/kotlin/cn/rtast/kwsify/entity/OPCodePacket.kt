/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/3
 */


package cn.rtast.kwsify.entity

import java.nio.ByteBuffer

data class OPCodePacket(val op: Int) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(4)
        return buffer.apply {
            putInt(op)
        }.array()
    }

    companion object {
        fun fromByteArray(buffer: ByteBuffer): OPCodePacket {
            val op = buffer.int
            return OPCodePacket(op)
        }
    }
}