/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.entity.Packet
import cn.rtast.kwsify.enums.OPCode
import cn.rtast.kwsify.util.fromJson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class Kwsify(private val address: String, private val channel: String) : IOperation {

    init {
        this.connect()
    }

    private lateinit var websocket: WebSocketClient
    private val subscribers = mutableMapOf<String, MutableList<Subscriber>>()

    override fun connect() {
        websocket = object : WebSocketClient(URI(address)) {
            override fun onOpen(handshakedata: ServerHandshake) {
                subscribePacket()
            }

            override fun onMessage(message: String) {
                val inboundPacket = message.fromJson<Packet>()
                val channel = inboundPacket.channel!!
                subscribers[channel]?.forEach { subscriber ->
                    subscriber.onMessage(channel, inboundPacket.body)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }
        }.apply { connect() }
    }

    override fun publish(payload: String): Boolean {
        val packet = Packet(OPCode.PUBLISH, payload, channel)
        subscribers[channel]?.forEach { subscriber ->
            websocket.send(packet)
        }
        return true
    }

    override fun subscribe(subscriber: Subscriber): Boolean {
        if (!subscribers.containsKey(channel)) {
            subscribers[channel] = mutableListOf()
        }
        subscribers[channel]?.add(subscriber)
        return true
    }

    override fun unsubscribe(): Boolean {
        if (subscribers.containsKey(channel)) {
            subscribers.remove(channel)
            unsubscribePacket()
            return true
        }
        return false
    }

    override fun subscribePacket() {
        val packet = Packet(OPCode.JOIN, "_subscribe", channel)
        websocket.send(packet)
    }

    override fun unsubscribePacket() {
        val packet = Packet(OPCode.EXIT_CHANNEL, "_unsubscribe", channel)
        websocket.send(packet)
    }
}
