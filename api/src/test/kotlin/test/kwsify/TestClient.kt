/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package test.kwsify

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.Subscriber
import cn.rtast.kwsify.entity.Packet

fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {
        override fun onMessage(channel: String, payload: String, packet: Packet) {
            println(packet.body)
        }
    })
    Thread.sleep(2000)
    while (true) {
        wsify.publish("test", "114514")
        Thread.sleep(1000)
    }
    wsify.unsubscribe("test")
}