package com.connan.kitchenassistant.data.chat

import com.connan.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ChatRepository {
    private val api = RetrofitClient.chatApiService

    private suspend fun bearerToken(): String {
        val token = supabase.auth.currentAccessTokenOrNull()
            ?: error("No active session — user must be logged in to chat")
        return "Bearer $token"
    }

    suspend fun getOrCreateThreadId(): String {
        chatCache.threadId?.let { return it }
        val token = bearerToken()
        val threads = api.listThreads(token)
        val thread = if (threads.isNotEmpty()) threads.first()
                     else api.createThread(token, CreateChatRequest())
        chatCache.threadId = thread.id
        return thread.id
    }

    suspend fun listAllThreads(): List<ChatThread> {
        return api.listThreads(bearerToken())
    }

    suspend fun createNewThread(): ChatThread {
        return api.createThread(bearerToken(), CreateChatRequest())
    }

    suspend fun loadMessages(threadId: String): List<ApiChatMessage> {
        val page = api.getMessages(bearerToken(), threadId)
        return page.messages
    }

    suspend fun loadMessagesBefore(threadId: String, before: Int): List<ApiChatMessage> {
        val page = api.getMessages(bearerToken(), threadId, before = before)
        return page.messages
    }

    suspend fun loadHistory(threadId: String): List<ApiChatMessage> {
        val result = api.getThread(bearerToken(), threadId)
        chatCache.saveMessages(result.messages)
        return result.messages
    }

    suspend fun sendThreadMessage(threadId: String, message: String): ApiChatMessage {
        val response = api.sendThreadMessage(
            token = bearerToken(),
            chatId = threadId,
            request = SendMessageRequest(message)
        )
        val updated = chatCache.getMessages() + response.userMessage + response.assistantMessage
        chatCache.saveMessages(updated)
        return response.assistantMessage
    }

    suspend fun sendVoiceMessage(threadId: String, audioFile: File): SendMessageResponse {
        val requestBody = audioFile.asRequestBody("audio/mp4".toMediaType())
        val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)
        val response = api.sendVoiceMessage(
            token = bearerToken(),
            chatId = threadId,
            audio = audioPart
        )
        val updated = chatCache.getMessages() + response.userMessage + response.assistantMessage
        chatCache.saveMessages(updated)
        return SendMessageResponse(
            thread = response.thread,
            userMessage = response.userMessage,
            assistantMessage = response.assistantMessage
        )
    }
}
