/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.entity.OutboundMessagePacket
import cn.rtast.kwsify.entity.PublishPacket
import cn.rtast.kwsify.entity.SubscribePacket
import cn.rtast.kwsify.entity.UnsubscribePacket
import cn.rtast.kwsify.enums.OPCode
import cn.rtast.kwsify.util.fromJson
import cn.rtast.kwsify.util.send
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Kwsify(private val address: String) : IOperation {
    private lateinit var websocket: WebSocketClient
    private val subscribers = mutableMapOf<String, MutableList<Subscriber>>()
    private val executor = Executors.newScheduledThreadPool(1)
    private val reconnectInterval = 5L
    private var isConnected = false

    private fun startReconnect() {
        executor.schedule({
            try {
                println("Reconnecting...")
                websocket.reconnect()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }, reconnectInterval, TimeUnit.SECONDS)
    }

    override fun connect(channel: String, broadcastSelf: Boolean) {
        websocket = object : WebSocketClient(URI(address)) {
            override fun onOpen(handshakedata: ServerHandshake) {
                this@Kwsify.isConnected = true
                val authPacket = SubscribePacket(OPCode.JOIN, UUID.randomUUID(), channel, broadcastSelf)
                websocket.send(authPacket)
            }

            override fun onMessage(message: String) {
                val inboundPacket = message.fromJson<OutboundMessagePacket>()
                val channel = inboundPacket.channel
                subscribers[channel]?.forEach { subscriber ->
                    subscriber.onMessage(channel, inboundPacket.body, inboundPacket)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                this@Kwsify.isConnected = false
                startReconnect()
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }
        }.apply { connect() }
    }

    override fun publish(channel: String, payload: String): Boolean {
        val packet = PublishPacket(OPCode.PUBLISH, payload, channel)
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
                val packet = UnsubscribePacket(OPCode.EXIT_CHANNEL, channel)
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
