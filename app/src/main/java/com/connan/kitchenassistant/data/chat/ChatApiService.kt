package com.connan.kitchenassistant.data.chat

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @GET("chats")
    suspend fun listThreads(
        @Header("Authorization") token: String
    ): List<ChatThread>

    @POST("chats")
    suspend fun createThread(
        @Header("Authorization") token: String,
        @Body request: CreateChatRequest
    ): ChatThread

    @GET("chats/{chat_id}")
    suspend fun getThread(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String
    ): ChatThreadWithMessages

    @GET("chats/{chat_id}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String,
        @Query("limit") limit: Int = 50,
        @Query("before") before: Int? = null
    ): ChatMessagesPage

    @POST("chats/{chat_id}/messages")
    suspend fun sendThreadMessage(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String,
        @Body request: SendMessageRequest
    ): SendMessageResponse

    @Multipart
    @POST("chats/{chat_id}/messages/voice")
    suspend fun sendVoiceMessage(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String,
        @Part audio: MultipartBody.Part
    ): VoiceMessageResponse
}
