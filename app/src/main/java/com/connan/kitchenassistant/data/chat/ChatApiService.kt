package com.connan.kitchenassistant.data.chat

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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

    @POST("chats/{chat_id}/messages")
    suspend fun sendThreadMessage(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String,
        @Body request: SendMessageRequest
    ): SendMessageResponse
}
