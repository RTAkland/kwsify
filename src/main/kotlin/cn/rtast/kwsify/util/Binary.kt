/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/3
 */


package cn.rtast.kwsify.util

import java.nio.ByteBuffer

fun ByteBuffer.putBoolean(value: Boolean) {
    put(if (value) 1.toByte() else 0.toByte())
}

fun ByteBuffer.getBoolean(): Boolean {
    return get() == 1.toByte()
}