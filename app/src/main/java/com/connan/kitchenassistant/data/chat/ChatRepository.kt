package com.connan.kitchenassistant.data.chat

import com.connan.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth

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
}
