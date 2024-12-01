/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package test.kwsify

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.Subscriber
import cn.rtast.kwsify.entity.OutboundMessagePacket

fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {
        override fun onMessage(channel: String, payload: String, packet: OutboundMessagePacket) {
            println(packet.body)
            Thread.sleep(500L)
            wsify.publish("test", "114514")
        }
    })
    Thread.sleep(1000L)
    wsify.publish("test", "114514")
}