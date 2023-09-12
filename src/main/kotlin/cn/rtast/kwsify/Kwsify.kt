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
import cn.rtast.kwsify.models.SessionModel
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class Kwsify(address: InetSocketAddress) : WebSocketServer(address) {

    companion object {
        val sessions = mutableListOf<SessionModel>()
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("New connection connected. ${conn.remoteSocketAddress}")
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
        val endpoint = conn.resourceDescriptor.replace("/", "")
        when (endpoint) {
            "subscribe" -> SubscribeEndpoint().onMessage(conn, message)
            "publish" -> PublishEndpoint().onMessage(conn, message)
        }
        println("Current session: $sessions")
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Server started.")
    }
}

fun main(args: Array<String>) {
    val server: Kwsify = if (args.isEmpty()) {
        Kwsify(InetSocketAddress("0.0.0.0", 5050))
    } else {
        Kwsify(InetSocketAddress(args[0], args[1].toInt()))
    }
    server.start()
}