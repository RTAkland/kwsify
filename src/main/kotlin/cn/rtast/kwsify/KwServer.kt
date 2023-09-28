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

import cn.rtast.kwsify.enums.ActionType
import cn.rtast.kwsify.enums.Endpoints
import cn.rtast.kwsify.enums.MsgType
import cn.rtast.kwsify.models.Action
import cn.rtast.kwsify.models.Reply
import cn.rtast.kwsify.models.Session
import cn.rtast.kwsify.utils.*
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

    private fun contains(conn: WebSocket): Boolean {
        sessions.forEach {
            if (it.session == conn) {
                return true
            }
        }
        return false
    }

    private fun contains(channel: String, clientId: String): Boolean {
        var flag = false
        sessions.forEach {
            if (it.channel == channel && it.clientId == clientId) {
                flag = true
            }
        }
        return flag
    }

    private fun removeSession(session: WebSocket) {
        var index = 0
        for (i in sessions) {
            if (i.session == session) break
            index++
        }
        sessions.removeAt(index)
    }

    private fun unsubscribe(channel: String, clientId: String) {
        var index = 0
        for (i in sessions) {
            if (i.channel == channel && i.clientId == clientId) break
            index++
        }
        sessions.removeAt(index)
    }

    private fun getIndex(conn: WebSocket): Int {
        var sessionIndex = 0
        sessions.forEachIndexed { index, session ->
            if (session.session == conn) {
                sessionIndex = index
            }
        }
        return sessionIndex
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println(
            "New websocket client connected: ${conn.localSocketAddress} | " + "endpoint: ${conn.resourceDescriptor}"
        )
        if (conn.resourceDescriptor.replace("/", "") !in listOf("subscribe", "publish")) {
            conn.send(Reply(getTimestamp(), MsgType.Notify, invalidEndpoint).toJsonString())
            conn.close()
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        this.removeSession(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        if (message.isEmpty()) {
            conn.send(Reply(getTimestamp(), MsgType.Notify, errorJsonSyntax).toJsonString())
            return
        }
        val msg = message.fromJson<Action>()
        val endpoint = conn.resourceDescriptor.replace("/", "")
        var clientId: String? = null

        if (msg.clientId.isNullOrEmpty() && !conf.randomClientId) {
            conn.send(Reply(getTimestamp(), MsgType.Notify, clientInvalid).toJsonString())

        } else if (msg.clientId.isNullOrEmpty() && conf.randomClientId) {
            clientId = genRndString(minimumClientIdLength)
        } else if (!msg.clientId.isNullOrEmpty()) {
            if (msg.clientId.length < minimumClientIdLength) {
                conn.send(
                    Reply(
                        getTimestamp(),
                        MsgType.Notify,
                        invalidClientIdLength + minimumClientIdLength
                    ).toJsonString()
                )
            } else {
                clientId = msg.clientId
            }
        }
        if (msg.action.lowercase() == ActionType.Subscribe.name.lowercase() && endpoint == Endpoints.Subscribe.name.lowercase()) {
            if (this.contains(conn)) {
                conn.send(Reply(getTimestamp(), MsgType.Notify, alreadyExists + msg.channel).toJsonString())
                return
            }
            conn.send(
                Reply(
                    getTimestamp(), MsgType.Notify, clientId!!
                ).toJsonString()
            )
            sessions.add(Session(msg.channel, clientId, conn))
            conn.send(Reply(getTimestamp(), MsgType.Notify, addedToQueueSuccessfully).toJsonString())
        } else if (msg.action.lowercase() == ActionType.Publish.name.lowercase()) {
            sessions.forEach {
                if (it.clientId == clientId && it.channel == msg.channel) {
                    it.session.send(Reply(getTimestamp(), MsgType.Message, msg.payload).toJsonString())
                    conn.send(Reply(getTimestamp(), MsgType.Notify, sentSuccessfully).toJsonString())
                }
            }
        } else if (msg.action.lowercase() == ActionType.Unsubscribe.name.lowercase() && endpoint == Endpoints.Subscribe.name.lowercase()) {
            if (this.contains(msg.channel, msg.clientId!!)) {
                this.unsubscribe(msg.channel, msg.clientId)
                conn.send(Reply(getTimestamp(), MsgType.Notify, successfullyUnsubscribed.replace("%", msg.clientId)).toJsonString())
            } else {
                conn.send(Reply(getTimestamp(), MsgType.Notify, invalidSubscribe).toJsonString())
            }
        } else if (msg.action.lowercase() == ActionType.ClientId.name.lowercase() && endpoint == Endpoints.Subscribe.name.lowercase()) {
            if (this.contains(conn)) {
                val sessionIndex = this.getIndex(conn)
                conn.send(Reply(getTimestamp(), MsgType.Notify, sessions[sessionIndex].clientId).toJsonString())
                return
            }
            conn.send(Reply(getTimestamp(), MsgType.Notify, invalidSubscribe).toJsonString())
        } else {
            conn.send(Reply(getTimestamp(), MsgType.Notify, unknownAction + msg.action.lowercase()).toJsonString())
        }
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Server started at [${this.address.address.hostAddress}:${this.address.port}]")
    }
}
