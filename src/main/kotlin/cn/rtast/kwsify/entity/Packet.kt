/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify.entity

data class Packet(
    val op: Int,
    val body: String,
    val channel: String?,
    val broadcastSelf: Boolean? = null,
    val sender: Sender? = null
) {
    data class Sender(
        val host: String,
        val port: Int,
        val address: String
    )
}