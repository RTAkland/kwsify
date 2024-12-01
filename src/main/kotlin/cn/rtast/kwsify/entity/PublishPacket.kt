/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

data class PublishPacket(
    val op: Int,
    val body: String,
    val channel: String
)