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
     * [payload] 为解析后的数据类实体
     * [rawPacket] 为原始二进制数据包
     */
    fun onMessage(channel: String, rawPacket: ByteArray, payload: OutboundMessageBytesPacket)

    /**
     * websocket连接断开时
     */
    fun onClosed(channel: String)

    /**
     * websocket连接打开时
     */
    fun onOpen(channel: String)

    /**
     * 当客户端发送心跳包时
     */
    fun onHeartbeat(channel: String) {}

    /**
     * 当接收到服务端返回的心跳包时
     */
    fun onHeartbeatReply(channel: String) {}
}