/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.util.toJson
import org.java_websocket.client.WebSocketClient

internal fun WebSocketClient.send(payload: Any) {
    this.send(payload.toJson())
}