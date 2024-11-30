/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify.util

import cn.rtast.kwsify.entity.Packet
import cn.rtast.kwsify.enums.OPCode
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class WebsocketServer(private val port: Int) : WebSocketServer(InetSocketAddress(port)) {

    private val connectionState = mutableMapOf<WebSocket, String>()

    private fun nullChannelPacket(conn: WebSocket) {
        val nullChannelPacket = Packet(OPCode.SYSTEM, "channel must not be null!", "_system").toJson()
        conn.send(nullChannelPacket)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val welcomePacket = Packet(OPCode.SYSTEM, "send OPCode=3, set channel to join channel", "_system").toJson()
        conn.send(welcomePacket)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        connectionState.remove(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        try {
            val packet = message.fromJson<Packet>()
            val channel = packet.channel
            when (packet.op) {
                OPCode.JOIN -> {
                    if (channel == null) nullChannelPacket(conn) else {
                        if (!connectionState.containsKey(conn)) {
                            connectionState[conn] = channel
                        } else {
                            val systemPacket =
                                Packet(OPCode.SYSTEM, "You already joined the channel ($channel)", "_system").toJson()
                            conn.send(systemPacket)
                        }
                    }
                }

                OPCode.SYSTEM -> {
                    connections.filter { it != conn }.forEach {
                        it.send(message)
                    }
                }

                OPCode.PUBLISH -> {
                    connectionState.filterKeys { it != conn }.filter { it.value == channel }.forEach {
                        val publishMessagePacket = Packet(OPCode.MESSAGE, packet.body, channel).toJson()
                        it.key.send(publishMessagePacket)
                    }
                }

                OPCode.EXIT_CHANNEL -> {
                    if (channel == null) nullChannelPacket(conn)
                    else if (connectionState.containsKey(conn)) connectionState.remove(conn)
                    else nullChannelPacket(conn)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Kwsify websocket server started on $port")
    }
}