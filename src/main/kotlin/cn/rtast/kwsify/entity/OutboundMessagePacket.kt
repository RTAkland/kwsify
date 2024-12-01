/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify.entity

import java.util.UUID

data class OutboundMessagePacket(
    val op: Int,
    val body: String,
    val channel: String,
    val sender: Sender?
) {
    data class Sender(
        val host: String,
        val port: Int,
        val address: String,
        val uuid: UUID
    )
}