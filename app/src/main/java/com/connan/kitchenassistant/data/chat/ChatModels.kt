package com.connan.kitchenassistant.data.chat

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("message") val message: String
)

data class ChatResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("reply") val reply: String,
    @SerializedName("tools_used") val toolsUsed: List<String> = emptyList()
)

data class CreateChatRequest(
    @SerializedName("title") val title: String? = null
)

data class ChatThread(
    @SerializedName("id") val id: String,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("title") val title: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
    @SerializedName("archived_at") val archivedAt: String?
)

data class ApiChatMessage(
    @SerializedName("id") val id: String,
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
    @SerializedName("sequence_no") val sequenceNo: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("tools_used") val toolsUsed: List<String> = emptyList()
)

data class ChatThreadWithMessages(
    @SerializedName("thread") val thread: ChatThread,
    @SerializedName("messages") val messages: List<ApiChatMessage>
)

data class SendMessageRequest(
    @SerializedName("message") val message: String
)

data class SendMessageResponse(
    @SerializedName("thread") val thread: ChatThread,
    @SerializedName("user_message") val userMessage: ApiChatMessage,
    @SerializedName("assistant_message") val assistantMessage: ApiChatMessage
)

data class VoiceMessageResponse(
    @SerializedName("transcription") val transcription: String,
    @SerializedName("thread") val thread: ChatThread,
    @SerializedName("user_message") val userMessage: ApiChatMessage,
    @SerializedName("assistant_message") val assistantMessage: ApiChatMessage
)

data class ChatMessagesPage(
    @SerializedName("messages") val messages: List<ApiChatMessage>,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("next_cursor") val nextCursor: Int?
)
