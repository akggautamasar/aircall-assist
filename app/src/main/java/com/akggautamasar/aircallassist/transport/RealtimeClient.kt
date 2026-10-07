package com.akggautamasar.aircallassist.transport

import com.akggautamasar.aircallassist.model.ChatMessage
import okhttp3.*
import org.json.JSONObject
import java.util.UUID

class RealtimeClient(
    private val deviceId: String,
    private val pairingCode: String,
    private val relayUrl: String,
    private val onMessage: (ChatMessage) -> Unit,
    private val onStatus: (String) -> Unit
) {
    private val client = OkHttpClient()
    private var socket: WebSocket? = null

    fun connect() {
        onStatus("Connecting…")
        val request = Request.Builder().url(relayUrl).build()
        socket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                val hello = JSONObject()
                    .put("type", "hello")
                    .put("deviceId", deviceId)
                    .put("code", pairingCode)
                webSocket.send(hello.toString())
                onStatus("Online")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val json = JSONObject(text)
                    if (json.optString("type") == "message") {
                        onMessage(ChatMessage(
                            id = json.getString("id"),
                            senderId = json.getString("senderId"),
                            text = json.getString("text"),
                            createdAtEpochMs = json.optLong("createdAt", System.currentTimeMillis()),
                            speak = json.optBoolean("speak", true)
                        ))
                    } else if (json.optString("type") == "status") {
                        onStatus(json.optString("value", "Connected"))
                    }
                }.onFailure { onStatus("Invalid message") }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onStatus("Offline")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                onStatus("Disconnected")
            }
        })
    }

    fun pairWith(targetCode: String) {
        socket?.send(JSONObject().put("type", "pair").put("targetCode", targetCode).toString())
    }

    fun send(text: String, speak: Boolean = true) {
        if (text.isBlank()) return
        socket?.send(
            JSONObject()
                .put("type", "message")
                .put("id", UUID.randomUUID().toString())
                .put("text", text)
                .put("speak", speak)
                .put("createdAt", System.currentTimeMillis())
                .toString()
        )
    }

    fun close() {
        socket?.close(1000, "bye")
        client.dispatcher.executorService.shutdown()
    }
}
