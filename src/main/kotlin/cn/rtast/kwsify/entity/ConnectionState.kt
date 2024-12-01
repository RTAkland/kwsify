/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify.entity

import org.java_websocket.WebSocket
import java.util.UUID

internal data class ConnectionState(
    val channel: String,
    val websocket: WebSocket,
    val broadcastSelf: Boolean,
    val uuid: UUID,
)