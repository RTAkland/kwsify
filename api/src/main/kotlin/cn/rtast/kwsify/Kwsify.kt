/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.entity.*
import cn.rtast.kwsify.enums.OPCode
import cn.rtast.kwsify.util.toJson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

class Kwsify(private val address: String) : IOperation {
    private lateinit var websocket: WebSocketClient
    private val subscribers = mutableMapOf<String, MutableList<Subscriber>>()

    override fun connect(channel: String, broadcastSelf: Boolean) {
        websocket = object : WebSocketClient(URI(address)) {
            override fun onOpen(handshakedata: ServerHandshake) {
                val authPacket = SubscribePacket(OPCode.JOIN, UUID.randomUUID(), channel, broadcastSelf).toByteArray()
                websocket.send(authPacket)
            }

            override fun onMessage(message: String) {
            }

            override fun onMessage(bytes: ByteBuffer) {
                val packet = OutboundMessageBytesPacket.fromByteArray(bytes.array())
                val channel = packet.channel
                subscribers[channel]?.forEach { subscriber ->
                    subscriber.onMessage(channel, bytes.array(), packet)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                subscribers[channel]?.forEach { subscriber ->
                    subscriber.onClosed(channel)
                }
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }
        }.apply { connect() }
    }

    override fun reconnect() {
        websocket.reconnect()
    }

    override fun publish(channel: String, payload: String): Boolean {
        return this.publish(channel, payload.toByteArray())
    }

    override fun publish(channel: String, payload: ByteArray): Boolean {
        val packet = PublishPacket(OPCode.PUBLISH, payload, channel).toByteArray()
        websocket.send(packet)
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
                val packet = UnsubscribePacket(OPCode.EXIT_CHANNEL, channel).toByteArray()
                websocket.send(packet)
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
