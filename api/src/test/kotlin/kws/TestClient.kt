/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/1/5
 */


package kws

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.Subscriber
import cn.rtast.kwsify.entity.OutboundMessageBytesPacket


fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {

        override fun onMessage(channel: String, rawPacket: ByteArray, packet: OutboundMessageBytesPacket) {
            println(String(packet.body))
        }

        override fun onClosed(channel: String) {
            println("closed")
            wsify.reconnect()
        }

        override fun onOpen(channel: String) {

        }
    })
    Thread.sleep(1000L)
    while (true) {
        wsify.publish("test", "114514")
        Thread.sleep(1000)
    }
}
