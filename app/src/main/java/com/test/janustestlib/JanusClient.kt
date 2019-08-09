package com.test.janustestlib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.*

interface JanusClient {

    suspend fun connectToJanus()

    suspend fun getRooms(): JanusPlugin

    suspend fun connectionToRoom(room: Long, name: String)

    suspend fun keepAlive()

    suspend fun createOffer(p0: SessionDescription?)

    suspend fun sendIceCandidate(p0: IceCandidate?)

}


class JanusManager(val server: JanusServer) : JanusClient {

    private var session: JanusSession? = null
    private var plugin: JanusSenderId? = null


    override suspend fun getRooms() = withContext(Dispatchers.IO) {
        server.sendJanusMessage(
            (session?.data?.id ?: 0).toString(), (plugin?.data?.id ?: 0).toString(),
            hashMapOf(
                "janus" to "message",
                "transaction" to UUID.randomUUID().toString(),
                "body" to hashMapOf("request" to "list")
            )
        )
    }

    override suspend fun connectionToRoom(room: Long, name: String) {
        withContext(Dispatchers.IO) {
            server.sendJanusMessage(
                (session?.data?.id ?: 0).toString(), (plugin?.data?.id ?: 0).toString(),
                hashMapOf(
                    "janus" to "message",
                    "transaction" to UUID.randomUUID().toString(),
                    "body" to hashMapOf(
                        "request" to "join",
                        "room" to 1234,
                        "display" to name
                    )
                )
            )
        }
    }

    override suspend fun connectToJanus() {
        if (session == null) {
            session = server.createSession(
                hashMapOf(
                    "janus" to "create",
                    "transaction" to UUID.randomUUID().toString()
                )
            )

            plugin = server.getSender(
                (session?.data?.id ?: 0).toString(),
                hashMapOf(
                    "janus" to "attach",
                    "transaction" to UUID.randomUUID().toString(),
                    "plugin" to "janus.plugin.audiobridge"
                )
            )


        }
    }

    override suspend fun keepAlive() {
        kotlinx.coroutines.delay(15_000)
        server.keepAlive(
            (session?.data?.id ?: 0).toString(),
            hashMapOf(
                "janus" to "keepALive",
                "transaction" to UUID.randomUUID().toString()
            )
        )
        keepAlive()
    }

    override suspend fun sendIceCandidate(p0: IceCandidate?) {
        withContext(Dispatchers.IO) {
            server.sendJanusMessage(
                (session?.data?.id ?: 0).toString(),
                (plugin?.data?.id ?: 0).toString(),
                hashMapOf(
                    "janus" to "trickle",
                    "transaction" to UUID.randomUUID().toString(),
                    "candidate" to hashMapOf(
                        "candidate" to p0.toString(),
                        "sdpMLineIndex" to p0?.sdpMLineIndex,
                        "sdpMid" to p0?.sdpMid
                    )
                )
            )
        }
    }

    override suspend fun createOffer(p0: SessionDescription?) {
        withContext(Dispatchers.IO) {
            server.sendJanusMessage(
                (session?.data?.id ?: 0).toString(),
                (plugin?.data?.id ?: 0).toString(),
                hashMapOf(
                    "janus" to "message",
                    "transaction" to UUID.randomUUID().toString(),
                    "body" to hashMapOf("request" to "configure", "muted" to true),
                    "jsep" to hashMapOf("type" to "offer", "sdp" to p0?.description)
                )
            )
        }
    }
}