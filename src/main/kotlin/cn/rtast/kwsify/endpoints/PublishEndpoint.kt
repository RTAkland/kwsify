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

package cn.rtast.kwsify.endpoints

import cn.rtast.kwsify.Kwsify
import cn.rtast.kwsify.models.Action
import com.google.gson.JsonSyntaxException
import org.java_websocket.WebSocket

class PublishEndpoint {

    fun onMessage(conn: WebSocket, message: Action) {
        try {
            if (message.clientId.length <= Kwsify.minClientIdLength) {
                conn.send(">Please make sure the length of client id is long than 25.")
            } else {
                val filteredSessions = Kwsify.sessions.filter { it.clientId == message.clientId }
                filteredSessions.forEach {
                    it.session.send(message.payload)
                }
                println("Published to ${message.channel} (${message.clientId})")
            }
        } catch (_: JsonSyntaxException) {
            conn.send(">Json syntax exception!")
        }
    }
}