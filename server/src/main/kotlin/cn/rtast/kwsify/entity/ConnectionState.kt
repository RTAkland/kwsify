/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/5
 */

@file:JvmName("ConnectionState")

package cn.rtast.kwsify.entity

import org.java_websocket.WebSocket
import java.util.UUID

internal data class ConnectionState(
    val channel: String,
    val websocket: WebSocket,
    val broadcastSelf: Boolean,
    val uuid: UUID,
)