package com.test.janustestlib

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface JanusServer {

    @POST("janus")
    suspend fun createSession(@Body parameters: Map<String, @JvmSuppressWildcards Any?>): JanusSession

    @POST("janus/{session_id}")
    suspend fun getSender(@Path("session_id") id: String, @Body parameters: Map<String, @JvmSuppressWildcards Any?>): JanusSenderId

    @POST("janus/{session_id}")
    suspend fun keepAlive(@Path("session_id") id: String, @Body parameters: Map<String, @JvmSuppressWildcards Any?>)

    @POST("janus/{session_id}/{sender_id}")
    suspend fun sendJanusMessage(@Path("session_id") id: String, @Path("sender_id") senderId: String, @Body parameters: Map<String, @JvmSuppressWildcards Any?>): JanusPlugin


}

open class JanusBaseData(open val janus: String, val error: JanusError? = null)

data class JanusError(val code: Int, val reason: String)

data class JanusSession(val janus: String, val error: JanusError? = null, val data: JanusDataId)

open class JanusSenderId(val janus: String, val error: JanusError? = null, val sessionId: Long, val data: JanusDataId)

data class JanusDataId(val id: Long)

data class JanusPlugin(
    val janus: String,
    val error: JanusError? = null,
    val sessionId: Long,
    val sender: Long,
    val plugindata: JanusPluginData
)

data class JanusPluginData(val plugin: String, val data: JanusPluginDeepData)

data class JanusPluginDeepData(val audiobridge: String, val list: List<Room>)

data class Room(
    val room: Long,
    val description: String,
    @SerializedName("sampling_rate") val samplingRate: Long,
    @SerializedName("pin_required") val pinRequired: Boolean,
    val record: Boolean,
    @SerializedName("num_participants") val numParticipants: Long
)