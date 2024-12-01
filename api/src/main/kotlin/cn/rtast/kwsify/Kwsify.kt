/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.entity.Packet
import cn.rtast.kwsify.enums.OPCode
import cn.rtast.kwsify.util.fromJson
import cn.rtast.kwsify.util.send
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class Kwsify(private val address: String) : IOperation {
    private lateinit var websocket: WebSocketClient
    private val subscribers = mutableMapOf<String, MutableList<Subscriber>>()

    override fun connect(channel: String, broadcastSelf: Boolean) {
        websocket = object : WebSocketClient(URI(address)) {
            override fun onOpen(handshakedata: ServerHandshake) {
                subscribePacket(channel, broadcastSelf)
            }

            override fun onMessage(message: String) {
                val inboundPacket = message.fromJson<Packet>()
                val channel = inboundPacket.channel!!
                subscribers[channel]?.forEach { subscriber ->
                    subscriber.onMessage(channel, inboundPacket.body, inboundPacket)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                unsubscribe(channel)
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }
        }.apply { connect() }
    }

    override fun publish(channel: String, payload: String): Boolean {
        val packet = Packet(OPCode.PUBLISH, payload, channel)
        subscribers[channel]?.forEach { subscriber ->
            websocket.send(packet)
        }
        return true
    }

    override fun subscribe(channel: String, broadcastSelf: Boolean, subscriber: Subscriber): Boolean {
        this.connect(channel, broadcastSelf)
        if (!subscribers.containsKey(channel)) {
            subscribers[channel] = mutableListOf()
        }
        subscribers[channel]?.add(subscriber)
        return true
    }

    override fun unsubscribe(channel: String): Boolean {
        try {

            if (subscribers.containsKey(channel)) {
                subscribers.remove(channel)
                unsubscribePacket(channel)
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun subscribePacket(channel: String, broadcastSelf: Boolean) {
        val packet = Packet(OPCode.JOIN, "_subscribe", channel, broadcastSelf)
        websocket.send(packet)
    }

    override fun unsubscribePacket(channel: String) {
        val packet = Packet(OPCode.EXIT_CHANNEL, "_unsubscribe", channel)
        websocket.send(packet)
    }
}
