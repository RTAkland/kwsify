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

import cn.rtast.kwsify.KwServer
import cn.rtast.kwsify.enums.MsgType
import cn.rtast.kwsify.models.Reply
import cn.rtast.kwsify.utils.getTimestamp
import cn.rtast.kwsify.utils.toJsonString
import org.java_websocket.WebSocket


class SubscriberListener {
    fun onEvent(connection: WebSocket, clientId: String, channel: String, payload: String) {
        KwServer.sessions.forEach {
            if (it.clientId == clientId && it.channel == channel) {
                connection.send(Reply(getTimestamp(), MsgType.Message, payload).toJsonString())
            }
        }
    }
}