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
    fun connect()

    /**
     * 发布消息
     */
    fun publish(payload: String): Boolean

    /**
     * 订阅频道消息
     */
    fun subscribe(subscriber: Subscriber): Boolean

    /**
     * 取消订阅频道消息
     */
    fun unsubscribe(): Boolean

    /**
     * 发送订阅频道的数据包
     */
    fun subscribePacket()

    /**
     * 发送取消订阅频道的数据包
     */
    fun unsubscribePacket()
}