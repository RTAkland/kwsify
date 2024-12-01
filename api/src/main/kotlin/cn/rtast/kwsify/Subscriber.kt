/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.entity.OutboundMessagePacket

interface Subscriber {
    /**
     * 接收到消息时
     */
    fun onMessage(channel: String, payload: String, packet: OutboundMessagePacket)

    /**
     * websocket连接断开时
     */
    fun onClosed(channel: String)
}