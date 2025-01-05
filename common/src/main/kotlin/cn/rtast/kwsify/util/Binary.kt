/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/5
 */


package cn.rtast.kwsify.util

import java.nio.ByteBuffer

internal fun ByteBuffer.putBoolean(value: Boolean) {
    put(if (value) 1.toByte() else 0.toByte())
}

internal fun ByteBuffer.getBoolean(): Boolean {
    return get() == 1.toByte()
}