/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify.util

import cn.rtast.kwsify.entity.ConnectionState
import cn.rtast.kwsify.entity.OutboundMessagePacket
import cn.rtast.kwsify.entity.SubscribePacket
import cn.rtast.kwsify.enums.OPCode
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class WebsocketServer(private val port: Int) : WebSocketServer(InetSocketAddress(port)) {

    private val connectionState = mutableListOf<ConnectionState>()

    private fun getSender(conn: WebSocket): OutboundMessagePacket.Sender {
        val state = connectionState.find { it.websocket == conn }!!
        val address = state.websocket.remoteSocketAddress.address.toString()
        val hostName = state.websocket.remoteSocketAddress.hostName
        val port = state.websocket.remoteSocketAddress.port
        return OutboundMessagePacket.Sender(hostName, port, address, state.uuid)
    }

    private fun nullChannelPacket(conn: WebSocket) {
        val nullChannelPacket =
            OutboundMessagePacket(OPCode.SYSTEM, "channel must not be null!", "_system", sender = this.getSender(conn))
        conn.send(nullChannelPacket)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val welcomePacket = OutboundMessagePacket(
            OPCode.SYSTEM,
            "send `op=3`, fill channel filed and uuid filed to join channel",
            "_system",
            null
        )
        conn.send(welcomePacket)
        println("New connection connected(${conn.remoteSocketAddress})(Unauthenticated)")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        connectionState.removeIf { it.websocket == conn }
        println("Connection closed(${conn.remoteSocketAddress}, $code, $reason)")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        try {
            val packet = message.fromJson<OutboundMessagePacket>()
            val channel = packet.channel
            when (packet.op) {
                OPCode.JOIN -> {
                    val authPacket = message.fromJson<SubscribePacket>()
                    if (!connectionState.any { it.websocket == conn }) {
                        connectionState.add(
                            ConnectionState(authPacket.channel, conn, authPacket.broadcastSelf, authPacket.uuid)
                        )
                    } else {
                        val systemPacket =
                            OutboundMessagePacket(
                                OPCode.SYSTEM,
                                "You already joined the channel ($channel)",
                                "_system",
                                sender = this.getSender(conn)
                            )
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
                    connectionState.forEach {
                        if (it.websocket != conn || it.broadcastSelf) {
                            if (it.channel == channel) {
                                val publishMessagePacket =
                                    OutboundMessagePacket(
                                        OPCode.MESSAGE, packet.body, channel,
                                        sender = this.getSender(conn)
                                    )
                                it.websocket.send(publishMessagePacket)
                            }
                        }
                    }
                }

                OPCode.EXIT_CHANNEL -> {
                    if (connectionState.any { it.websocket == conn })
                        connectionState.removeIf { it.websocket == conn }
                    else nullChannelPacket(conn)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val outboundPacket = OutboundMessagePacket(OPCode.SYSTEM, e.message.toString(), "_system", null)
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