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

import cn.rtast.kwsify.endpoints.PublishEndpoint
import cn.rtast.kwsify.endpoints.SubscribeEndpoint
import cn.rtast.kwsify.enums.ActionType
import cn.rtast.kwsify.models.Action
import cn.rtast.kwsify.models.Config
import cn.rtast.kwsify.models.Session
import cn.rtast.kwsify.utils.ArgumentsParser
import cn.rtast.kwsify.utils.ConfigUtil
import cn.rtast.kwsify.utils.fromJson
import com.google.gson.JsonSyntaxException
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class Kwsify(address: InetSocketAddress) : WebSocketServer(address) {

    companion object {
        private val validEndpoints = listOf("subscribe", "publish")

        val sessions = mutableListOf<Session>()

        private val conf = ConfigUtil().readConf()

        val minClientIdLength = this.conf.minClientIdLength
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val endpoint = conn.resourceDescriptor.replace("/", "")
        if (!validEndpoints.contains(endpoint)) {
            conn.close(1000, "This endpoint is invalid.")
        } else {
            println("New connection connected. ${conn.remoteSocketAddress}")
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        val endpoint = conn.resourceDescriptor.replace("/", "")
        if (endpoint == "subscribe") {
            sessions.forEachIndexed { i, s ->
                if (s.session == conn) {
                    sessions.removeAt(i)
                }
            }
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        val endpoint = conn.resourceDescriptor.replace("/", "").lowercase()
        try {
            val jsonPayload = message.fromJson<Action>()
            val action = jsonPayload.action.name.lowercase()
            if (action == ActionType.Publish.name.lowercase() && endpoint == "publish") {
                PublishEndpoint().onMessage(conn, jsonPayload)
            } else if (action == ActionType.Subscribe.name.lowercase() && endpoint == "subscribe") {
                SubscribeEndpoint().onMessage(conn, jsonPayload)
            } else if (action == ActionType.Unsubscribe.name.lowercase() && endpoint == "subscribe") {
                try {
                    if (!sessions.contains(Session(jsonPayload.channel, jsonPayload.clientId, conn))) {
                        conn.send(">Already unsubscribed!")
                    } else {
                        sessions.forEachIndexed { i, s ->
                            if (s.session == conn) {
                                sessions.removeAt(i)
                            }
                        }
                    }
                } catch (_: ConcurrentModificationException) {
                    println("Removed")
                }
            } else {
                conn.send("Unknown operation!")
                println("Unknown operation!")
            }
        } catch (_: JsonSyntaxException) {
            conn.send(">Json syntax exception!")
        } catch (_: NullPointerException) {
            conn.send(">Json syntax exception")
        } finally {
            println("Current sessions: $sessions")
        }
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Server started at [${this.address.address.hostAddress}:${this.address.port}]")
    }
}

fun main(args: Array<String>?) {
    val conf: Config = if (args.isNullOrEmpty()) {
        println("Args via config file.")
        ConfigUtil().readConf()
    } else {
        println("Args via command line.")
        ArgumentsParser(args).parse()
    }
    val server = Kwsify(InetSocketAddress(conf.host, conf.port))
    server.start()
}