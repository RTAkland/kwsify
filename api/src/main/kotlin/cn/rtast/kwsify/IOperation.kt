/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

interface IOperation {

    /**
     * 连接Websocket
     */
    fun connect(channel: String, broadcastSelf: Boolean)

    /**
     * 重连Websocket服务器
     */
    fun reconnect()

    /**
     * 发布消息
     */
    fun publish(channel: String, payload: String): Boolean

    /**
     * 发布二进制消息
     */
    fun publish(channel: String, payload: ByteArray): Boolean

    /**
     * 订阅频道消息
     */
    fun subscribe(channel: String, broadcastSelf: Boolean, subscriber: Subscriber): Boolean

    /**
     * 取消订阅频道消息
     */
    fun unsubscribe(channel: String): Boolean
}