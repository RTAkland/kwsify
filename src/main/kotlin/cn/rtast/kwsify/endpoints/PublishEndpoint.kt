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
import cn.rtast.kwsify.models.PublishModel
import cn.rtast.kwsify.models.response.SyntaxErrorResponse
import cn.rtast.kwsify.utils.fromJson
import cn.rtast.kwsify.utils.toJsonString
import com.google.gson.JsonSyntaxException
import org.java_websocket.WebSocket

class PublishEndpoint {

    fun onMessage(conn: WebSocket, message: String) {
        try {
            val publishModel = message.fromJson<PublishModel>()
            Kwsify.sessions.forEach {
                it.session.send(PublishModel(publishModel.channel, publishModel.payload).toJsonString())
            }
        } catch (_: JsonSyntaxException) {
            conn.send(
                SyntaxErrorResponse(
                    5002, "Json syntax error! Syntax: {\"channel\": \"<Your channel here>\"}"
                ).toJsonString()
            )
        }
    }
}