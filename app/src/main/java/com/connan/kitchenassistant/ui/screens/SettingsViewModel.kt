package com.connan.kitchenassistant.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.auth.AuthRepository
import com.connan.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val email: String = "",
    val userId: String = "",
    val isSigningOut: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val user = supabase.auth.currentUserOrNull()
        _uiState.update {
            it.copy(
                email  = user?.email.orEmpty(),
                userId = user?.id.orEmpty()
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true, error = null) }
            try {
                authRepository.signOut()
                // MainActivity observes SessionStatus and redirects to LoginScreen automatically
            } catch (e: Exception) {
                _uiState.update { it.copy(isSigningOut = false, error = "Could not sign out. Try again.") }
            }
        }
    }
}
