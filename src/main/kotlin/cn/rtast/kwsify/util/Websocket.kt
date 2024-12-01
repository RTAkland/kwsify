/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify.util

import org.java_websocket.WebSocket

fun WebSocket.send(payload: Any) {
    this.send(payload.toJson())
}