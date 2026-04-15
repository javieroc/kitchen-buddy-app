package com.connan.kitchenassistant.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.chat.ChatRepository
import com.connan.kitchenassistant.data.chat.ChatThread
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConversationsUiState(
    val threads: List<ChatThread> = emptyList(),
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val error: String? = null
)

class ConversationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository()

    private val _uiState = MutableStateFlow(ConversationsUiState())
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    private val _navigateToChat = MutableSharedFlow<ChatThread>(extraBufferCapacity = 1)
    val navigateToChat: SharedFlow<ChatThread> = _navigateToChat.asSharedFlow()

    init {
        loadThreads()
    }

    fun loadThreads() {
        viewModelScope.launch {
            // Only show the full-screen spinner on the very first load (no data yet).
            // Subsequent refreshes (tab switches, back-navigation) update silently.
            val initialLoad = _uiState.value.threads.isEmpty()
            if (initialLoad) _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val threads = repository.listAllThreads()
                _uiState.update { it.copy(threads = threads, isLoading = false) }
            } catch (e: Exception) {
                if (initialLoad) {
                    _uiState.update { it.copy(isLoading = false, error = "Could not load conversations.") }
                }
            }
        }
    }

    fun onThreadClick(thread: ChatThread) {
        _navigateToChat.tryEmit(thread)
    }

    fun createNewThread() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, error = null) }
            try {
                val thread = repository.createNewThread()
                _uiState.update { state ->
                    state.copy(
                        threads = listOf(thread) + state.threads,
                        isCreating = false
                    )
                }
                _navigateToChat.tryEmit(thread)
            } catch (e: Exception) {
                _uiState.update { it.copy(isCreating = false, error = "Could not create conversation.") }
            }
        }
    }
}
