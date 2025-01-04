/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/3
 */

@file:Suppress("unused")

package cn.rtast.kwsify

import java.util.Base64

fun String.encodeToBase64(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray(Charsets.UTF_8))
}

fun ByteArray.encodeToBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun String.decodeToString(): String {
    return String(Base64.getDecoder().decode(this), Charsets.UTF_8)
}

fun String.decodeToByteArray(): ByteArray {
    return Base64.getDecoder().decode(this)
}