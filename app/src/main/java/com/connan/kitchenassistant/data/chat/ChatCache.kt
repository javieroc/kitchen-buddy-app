package com.connan.kitchenassistant.data.chat

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

lateinit var chatCache: ChatCache

fun initChatCache(context: Context) {
    chatCache = ChatCache(context)
}

class ChatCache(context: Context) {
    private val prefs = context.getSharedPreferences("chat_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    var threadId: String?
        get() = prefs.getString(KEY_THREAD_ID, null)
        set(value) { prefs.edit().putString(KEY_THREAD_ID, value).apply() }

    fun getMessages(): List<ApiChatMessage> {
        val json = prefs.getString(KEY_MESSAGES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ApiChatMessage>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveMessages(messages: List<ApiChatMessage>) {
        prefs.edit().putString(KEY_MESSAGES, gson.toJson(messages)).apply()
    }

    companion object {
        private const val KEY_THREAD_ID = "thread_id"
        private const val KEY_MESSAGES = "messages"
    }
}
