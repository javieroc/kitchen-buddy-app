package com.connan.kitchenassistant.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.chat.ApiChatMessage
import com.connan.kitchenassistant.data.chat.ChatRepository
import com.connan.kitchenassistant.data.chat.chatCache
import com.connan.kitchenassistant.ui.components.ChatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


private const val PAGE_SIZE = 10

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val hasMoreMessages: Boolean = false,
    val isLoading: Boolean = false,
    val isInitializing: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Signals the screen to scroll to the bottom (index 0 with reverseLayout).
    // Only emitted when the user sends a message — they should always see their own message.
    private val _scrollToBottom = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToBottom: SharedFlow<Unit> = _scrollToBottom.asSharedFlow()

    private var threadId: String? = null
    private var allMessages: List<ChatMessage> = emptyList()
    private var shownCount = PAGE_SIZE

    init {
        initChat()
    }

    private fun initChat() {
        viewModelScope.launch {
            val cachedId = chatCache.threadId
            val cachedMessages = chatCache.getMessages()

            if (cachedId != null && cachedMessages.isNotEmpty()) {
                // Cache hit — show immediately, refresh in background
                threadId = cachedId
                allMessages = cachedMessages.map { it.toUiMessage() }
                shownCount = minOf(PAGE_SIZE, allMessages.size)
                refreshDisplayed()
                _uiState.update { it.copy(isInitializing = false) }

                refreshFromNetwork(cachedId)
            } else {
                // No cache — fetch from network
                try {
                    val id = repository.getOrCreateThreadId()
                    threadId = id
                    val msgs = repository.loadHistory(id)
                    allMessages = msgs.map { it.toUiMessage() }
                    shownCount = minOf(PAGE_SIZE, allMessages.size)
                    refreshDisplayed()
                    _uiState.update { it.copy(isInitializing = false) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isInitializing = false, error = "Could not load conversation.") }
                }
            }
        }
    }

    private suspend fun refreshFromNetwork(id: String) {
        try {
            val fresh = repository.loadHistory(id)
            val freshMapped = fresh.map { it.toUiMessage() }
            when {
                freshMapped.size > allMessages.size -> {
                    val delta = freshMapped.size - allMessages.size
                    allMessages = freshMapped
                    shownCount += delta
                    refreshDisplayed()
                }
                freshMapped.size < allMessages.size -> {
                    allMessages = freshMapped
                    shownCount = minOf(shownCount, allMessages.size)
                    refreshDisplayed()
                }
            }
        } catch (_: Exception) {
            // Keep cached data on network failure
        }
    }

    fun loadMore() {
        val prevShown = shownCount
        shownCount = minOf(shownCount + PAGE_SIZE, allMessages.size)
        val prepended = shownCount - prevShown
        if (prepended > 0) {
            refreshDisplayed()
            // No scroll command needed — reverseLayout naturally keeps the user's position
            // when items are appended to the end (visual top) of the list.
        }
    }

    private fun refreshDisplayed() {
        val total = allMessages.size
        val from = maxOf(0, total - shownCount)
        _uiState.update {
            it.copy(
                messages = allMessages.subList(from, total),
                hasMoreMessages = from > 0
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentThreadId = threadId ?: return

        val userMsg = ChatMessage(text = text, isFromUser = true)
        allMessages = allMessages + userMsg
        shownCount++
        refreshDisplayed()
        _uiState.update { it.copy(isLoading = true, error = null) }
        _scrollToBottom.tryEmit(Unit)

        viewModelScope.launch {
            try {
                val apiReply = repository.sendThreadMessage(currentThreadId, text)
                val assistantMsg = apiReply.toUiMessage()
                allMessages = allMessages + assistantMsg
                shownCount++
                refreshDisplayed()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                // Roll back the optimistic user message
                allMessages = allMessages.dropLast(1)
                shownCount--
                refreshDisplayed()
                _uiState.update { it.copy(isLoading = false, error = "Failed to get a response. Please try again.") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

private fun ApiChatMessage.toUiMessage() = ChatMessage(
    text = content,
    isFromUser = role == "user"
)
