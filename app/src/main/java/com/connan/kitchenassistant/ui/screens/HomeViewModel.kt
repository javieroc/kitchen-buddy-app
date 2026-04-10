package com.connan.kitchenassistant.ui.screens

import android.app.Application
import android.media.MediaRecorder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.chat.ApiChatMessage
import com.connan.kitchenassistant.data.chat.ChatRepository
import com.connan.kitchenassistant.data.chat.chatCache
import com.connan.kitchenassistant.ui.components.ChatMessage
import java.io.File
import java.util.UUID
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
    val isRecording: Boolean = false,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _scrollToBottom = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToBottom: SharedFlow<Unit> = _scrollToBottom.asSharedFlow()

    private var threadId: String? = null
    private var allMessages: List<ChatMessage> = emptyList()
    private var shownCount = PAGE_SIZE

    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null

    init {
        initChat()
    }

    private fun initChat() {
        viewModelScope.launch {
            val cachedId = chatCache.threadId
            val cachedMessages = chatCache.getMessages()

            if (cachedId != null && cachedMessages.isNotEmpty()) {
                threadId = cachedId
                allMessages = cachedMessages.map { it.toUiMessage() }
                shownCount = minOf(PAGE_SIZE, allMessages.size)
                refreshDisplayed()
                _uiState.update { it.copy(isInitializing = false) }
                refreshFromNetwork(cachedId)
            } else {
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
        } catch (_: Exception) {}
    }

    fun loadMore() {
        val prevShown = shownCount
        shownCount = minOf(shownCount + PAGE_SIZE, allMessages.size)
        if (shownCount - prevShown > 0) refreshDisplayed()
    }

    private fun refreshDisplayed() {
        val total = allMessages.size
        val from = maxOf(0, total - shownCount)
        _uiState.update {
            it.copy(
                messages = allMessages.subList(from, total).reversed(),
                hasMoreMessages = from > 0
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentThreadId = threadId ?: return

        val userMsg = ChatMessage(id = UUID.randomUUID().toString(), text = text, isFromUser = true)
        allMessages = allMessages + userMsg
        shownCount++
        refreshDisplayed()
        _uiState.update { it.copy(isLoading = true, error = null) }
        _scrollToBottom.tryEmit(Unit)

        viewModelScope.launch {
            try {
                val apiReply = repository.sendThreadMessage(currentThreadId, text)
                allMessages = allMessages + apiReply.toUiMessage()
                shownCount++
                refreshDisplayed()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                allMessages = allMessages.dropLast(1)
                shownCount--
                refreshDisplayed()
                _uiState.update { it.copy(isLoading = false, error = "Failed to get a response. Please try again.") }
            }
        }
    }

    // ── voice recording ──────────────────────────────────────────────────────

    fun startRecording() {
        val file = File(getApplication<Application>().cacheDir, "voice_message.m4a")
        recordingFile = file
        try {
            mediaRecorder = MediaRecorder(getApplication<Application>()).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            _uiState.update { it.copy(isRecording = true, error = null) }
        } catch (e: Exception) {
            mediaRecorder?.release()
            mediaRecorder = null
            file.delete()
            recordingFile = null
            _uiState.update { it.copy(error = "Could not start recording.") }
        }
    }

    fun stopRecordingAndSend() {
        if (!_uiState.value.isRecording) return

        mediaRecorder?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        mediaRecorder = null

        val file = recordingFile ?: run {
            _uiState.update { it.copy(isRecording = false) }
            return
        }
        recordingFile = null

        val currentThreadId = threadId ?: run {
            _uiState.update { it.copy(isRecording = false) }
            file.delete()
            return
        }

        _uiState.update { it.copy(isRecording = false, isLoading = true, error = null) }
        _scrollToBottom.tryEmit(Unit)

        viewModelScope.launch {
            try {
                val response = repository.sendVoiceMessage(currentThreadId, file)
                allMessages = allMessages + response.userMessage.toUiMessage() + response.assistantMessage.toUiMessage()
                shownCount += 2
                refreshDisplayed()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to send voice message. Try again.") }
            } finally {
                file.delete()
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        mediaRecorder = null
        recordingFile?.delete()
        recordingFile = null
    }
}

private fun ApiChatMessage.toUiMessage() = ChatMessage(
    id = id,
    text = content,
    isFromUser = role == "user"
)
