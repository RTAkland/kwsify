/*
 * Copyright 2023 RTAkland
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package cn.rtast.kwsify

import cn.rtast.kwsify.endpoints.PublisherListener
import cn.rtast.kwsify.endpoints.SubscriberListener
import cn.rtast.kwsify.enums.ActionType
import cn.rtast.kwsify.enums.Endpoints
import cn.rtast.kwsify.enums.MsgType
import cn.rtast.kwsify.models.Action
import cn.rtast.kwsify.models.Reply
import cn.rtast.kwsify.models.Session
import cn.rtast.kwsify.utils.*
import com.google.gson.JsonSyntaxException
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class KwServer(address: InetSocketAddress) : WebSocketServer(address) {

    companion object {
        val sessions = mutableListOf<Session>()

        private val conf = ConfigUtil().readConf()

        val minimumClientIdLength = this.conf.minClientIdLength
    }

    private var endpoint = "/"
    private var endpointType = Endpoints.Subscribe

    private fun contains(conn: WebSocket): Boolean {
        sessions.forEach {
            if (it.session == conn) {
                return true
            }
        }
        return false
    }

    private fun removeSession(session: WebSocket) {
        sessions.forEachIndexed { index, s ->
            if (s.session == session) {
                sessions.removeAt(index)
            }
        }
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println(
            "New websocket client connected: ${conn.localSocketAddress} | " + "endpoint: ${conn.resourceDescriptor}"
        )
        this.endpoint = conn.resourceDescriptor.replace("/", "").lowercase()
        when (this.endpoint) {
            "subscribe" -> {
                this.endpointType = Endpoints.Subscribe
            }

            "publish" -> {
                this.endpointType = Endpoints.Publish
            }

            else -> {
                this.endpointType = Endpoints.Subscribe
            }
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        if (this.endpointType == Endpoints.Subscribe) {
            if (this.contains(conn)) {
                this.removeSession(conn)
            }
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        try {
            val msg = message.fromJson<Action>()
            val clientId =
                if (msg.clientId != null && msg.clientId.length >= minimumClientIdLength) msg.clientId else genRndString(
                    minimumClientIdLength
                )
            if (msg.action == ActionType.Subscribe) {
                if (this.contains(conn)) {
                    conn.send(Reply(getTimestamp(), MsgType.Notify, alreadyExists + msg.channel).toJsonString())
                    return
                }
                SubscriberListener().onEvent(conn, clientId, msg.channel, msg.payload)
            } else if (msg.action == ActionType.Publish) {
                PublisherListener().onEvent(conn, clientId, msg.channel, msg.payload)
            } else {
                conn.send(Reply(getTimestamp(), MsgType.Notify, invalidEndpoint).toJsonString())
                conn.close()
            }
        } catch (_: JsonSyntaxException) {
            conn.send(Reply(getTimestamp(), MsgType.Notify, errorJsonSyntax).toJsonString())
        }
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Server started at [${this.address.address.hostAddress}:${this.address.port}]")
    }
}
