package com.example.kitchenassistant.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitchenassistant.data.auth.AuthRepository
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val accessToken: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter your email and password")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            _uiState.value = try {
                val token = authRepository.signIn(email, password)
                LoginUiState.Success(token)
            } catch (e: RestException) {
                // Supabase returns structured errors — message is user-friendly
                LoginUiState.Error(e.message ?: "Invalid email or password")
            } catch (e: Exception) {
                // Network errors, timeouts, etc.
                LoginUiState.Error("Connection error. Please check your internet and try again.")
            }
        }
    }

    fun clearError() {
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }
}
