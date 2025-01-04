/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */

@file:JvmName("Subscriber")

package cn.rtast.kwsify

import cn.rtast.kwsify.entity.OutboundMessageBytesPacket

interface Subscriber {
    /**
     * 接收到二进制消息时
     */
    fun onMessage(channel: String, payload: ByteArray, packet: OutboundMessageBytesPacket) {}

    /**
     * websocket连接断开时
     */
    fun onClosed(channel: String)
}