/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

import java.util.UUID

data class SubscribePacket(
    val op: Int,
    val uuid: UUID,
    val channel: String,
    val broadcastSelf: Boolean
)