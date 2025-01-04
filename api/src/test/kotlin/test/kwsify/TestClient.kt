/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package test.kwsify

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.Subscriber
import cn.rtast.kwsify.entity.OutboundMessageBytesPacket

fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {

        override fun onMessage(channel: String, payload: ByteArray, packet: OutboundMessageBytesPacket) {
            println(String(packet.body))
        }

        override fun onClosed(channel: String) {
            println("closed")
            wsify.reconnect()
        }
    })
    Thread.sleep(1000L)
    while (true) {
        wsify.publish("test", "114514")
        Thread.sleep(1000)
    }
}