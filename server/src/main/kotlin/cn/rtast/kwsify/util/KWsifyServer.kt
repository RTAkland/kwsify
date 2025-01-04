/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */

@file:JvmName("KWsifyServer")

package cn.rtast.kwsify.util

import cn.rtast.kwsify.entity.*
import cn.rtast.kwsify.enums.OPCode
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*

class KWsifyServer(private val port: Int) : WebSocketServer(InetSocketAddress(port)) {

    private val connectionState = mutableListOf<ConnectionState>()

    private fun getSender(conn: WebSocket): OutboundMessageBytesPacket.Sender {
        val state = connectionState.find { it.websocket == conn }!!
        val address = state.websocket.remoteSocketAddress.address.toString()
        val hostName = state.websocket.remoteSocketAddress.hostName
        val port = state.websocket.remoteSocketAddress.port
        return OutboundMessageBytesPacket.Sender(hostName, port, address, state.uuid)
    }

    private fun getSystemSender(): OutboundMessageBytesPacket.Sender {
        return OutboundMessageBytesPacket.Sender("system", 0, "system", UUID.randomUUID())
    }

    private fun nullChannelPacket(conn: WebSocket) {
        val nullChannelPacket =
            OutboundMessageBytesPacket(
                OPCode.SYSTEM,
                "channel must not be null!".toByteArray(),
                "_system",
                sender = this.getSender(conn)
            ).toByteArray()
        conn.send(nullChannelPacket)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val welcomePacket = OutboundMessageBytesPacket(
            OPCode.SYSTEM,
            "send `op=3`, fill channel filed and uuid filed to join channel".toByteArray(),
            "_system", getSystemSender()
        ).toByteArray()
        conn.send(welcomePacket)
        println("New connection connected(${conn.remoteSocketAddress})(Unauthenticated)")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        connectionState.removeIf { it.websocket == conn }
        println("Connection closed(${conn.remoteSocketAddress}, $code, $reason)")
    }

    override fun onMessage(conn: WebSocket, message: String) {
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        try {
            val packet = OPCodePacket.fromByteArray(message.duplicate())
            when (packet.op) {
                OPCode.JOIN -> {
                    val authPacket = SubscribePacket.fromByteArray(message.duplicate())
                    if (!connectionState.any { it.websocket == conn }) {
                        connectionState.add(
                            ConnectionState(authPacket.channel, conn, authPacket.broadcastSelf, authPacket.uuid)
                        )
                    } else {
                        val systemPacket =
                            OutboundMessageBytesPacket(
                                OPCode.SYSTEM,
                                "You already joined the channel (${authPacket.channel})".toByteArray(),
                                "_system",
                                sender = this.getSender(conn)
                            ).toByteArray()
                        conn.send(systemPacket)
                    }
                    println("New authed connection joined the channel(${authPacket.channel}) with UUID(${authPacket.uuid})")
                }

                OPCode.SYSTEM -> {
                    connections.filter { it != conn }.forEach {
                        it.send(message)
                    }
                }

                OPCode.PUBLISH -> {
                    val packet = PublishPacket.fromByteArray(message.duplicate())
                    connectionState.forEach {
                        if (it.websocket != conn || it.broadcastSelf) {
                            if (it.channel == packet.channel) {
                                val publishMessagePacket =
                                    OutboundMessageBytesPacket(
                                        OPCode.MESSAGE, packet.body, packet.channel,
                                        sender = this.getSender(conn)
                                    ).toByteArray()
                                it.websocket.send(publishMessagePacket)
                            }
                        }
                    }
                }

                OPCode.EXIT_CHANNEL -> {
                    val connection = connectionState.find { it.websocket == conn }!!
                    if (connectionState.any { it.websocket == conn })
                        connectionState.removeIf { it.websocket == conn }
                    else nullChannelPacket(conn)
                    println("Connection unsubscribed(${conn.remoteSocketAddress}, ${connection.uuid}: ${connection.channel})")
                }

                OPCode.HEARTBEAT -> {
                    val heartbeatPacket =
                        OutboundMessageBytesPacket(
                            OPCode.HEARTBEAT_REPLY,
                            "heartbeat".toByteArray(),
                            "_system",
                            sender = this.getSender(conn)
                        ).toByteArray()
                    conn.send(heartbeatPacket)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val outboundPacket =
                OutboundMessageBytesPacket(
                    OPCode.SYSTEM,
                    e.message.toString().toByteArray(),
                    "_system",
                    getSystemSender()
                ).toByteArray()
            conn.send(outboundPacket)
        }
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Kwsify websocket server started on $port")
    }
}