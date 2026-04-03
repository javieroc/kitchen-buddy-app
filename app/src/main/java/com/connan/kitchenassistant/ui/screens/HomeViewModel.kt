package com.connan.kitchenassistant.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.chat.ChatRepository
import com.connan.kitchenassistant.ui.components.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isInitializing: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var threadId: String? = null

    init {
        initThread()
    }

    private fun initThread() {
        viewModelScope.launch {
            try {
                val thread = repository.getOrCreateThread()
                threadId = thread.id
                val history = repository.loadHistory(thread.id)
                _uiState.update { it.copy(messages = history, isInitializing = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isInitializing = false,
                        error = "Could not load conversation history."
                    )
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentThreadId = threadId ?: return

        val userMessage = ChatMessage(text = text, isFromUser = true)
        _uiState.update { it.copy(messages = it.messages + userMessage, isLoading = true, error = null) }

        viewModelScope.launch {
            _uiState.update { state ->
                try {
                    val assistantMessage = repository.sendThreadMessage(currentThreadId, text)
                    state.copy(messages = state.messages + assistantMessage, isLoading = false)
                } catch (e: Exception) {
                    state.copy(
                        isLoading = false,
                        error = "Failed to get a response. Please try again."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
