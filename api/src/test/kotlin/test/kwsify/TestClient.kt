/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package test.kwsify

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.Subscriber

fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8989", "test")
    wsify.subscribe(object : Subscriber {
        override fun onMessage(channel: String, payload: String) {
            println(payload)
        }
    })
    Thread.sleep(2000)
    while (true) {
        wsify.publish("test")
        Thread.sleep(1000)
    }
    wsify.unsubscribe()
}