package com.example.kitchenassistant.data.auth

import com.example.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    // Sign in with email + password.
    // Matches the exact API from the docs:
    //   supabase.auth.signInWith(Email) { email = "..."; password = "..." }
    // Throws RestException on wrong credentials, or network errors on connectivity issues.
    suspend fun signIn(email: String, password: String): String {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        // After a successful signInWith the session is stored internally by the SDK.
        // We retrieve the access token to forward to our own backend.
        return supabase.auth.currentAccessTokenOrNull()
            ?: error("Sign in succeeded but no access token was returned")
    }

    // Sign out and clear the local session
    suspend fun signOut() {
        supabase.auth.signOut()
    }

    // Check if a valid session already exists (for auto-login on app restart)
    fun isLoggedIn(): Boolean = supabase.auth.currentSessionOrNull() != null

    // Get the current access token — call this whenever you need to attach
    // it as a Bearer token to your own backend requests
    fun currentToken(): String? = supabase.auth.currentAccessTokenOrNull()
}
