/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

data class UnsubscribePacket(
    val op: Int,
    val channel: String,
)