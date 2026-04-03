package com.example.kitchenassistant.data.chat

import com.example.kitchenassistant.data.supabase
import com.example.kitchenassistant.ui.components.ChatMessage
import io.github.jan.supabase.auth.auth

class ChatRepository {
    private val api = RetrofitClient.chatApiService

    private suspend fun bearerToken(): String {
        val token = supabase.auth.currentAccessTokenOrNull()
            ?: error("No active session — user must be logged in to chat")
        return "Bearer $token"
    }

    suspend fun getOrCreateThread(): ChatThread {
        val token = bearerToken()
        val threads = api.listThreads(token)
        return if (threads.isNotEmpty()) threads.first()
        else api.createThread(token, CreateChatRequest())
    }

    suspend fun loadHistory(threadId: String): List<ChatMessage> {
        val result = api.getThread(bearerToken(), threadId)
        return result.messages.map { msg ->
            ChatMessage(text = msg.content, isFromUser = msg.role == "user")
        }
    }

    suspend fun sendThreadMessage(threadId: String, message: String): ChatMessage {
        val response = api.sendThreadMessage(
            token = bearerToken(),
            chatId = threadId,
            request = SendMessageRequest(message)
        )
        return ChatMessage(text = response.assistantMessage.content, isFromUser = false)
    }
}
