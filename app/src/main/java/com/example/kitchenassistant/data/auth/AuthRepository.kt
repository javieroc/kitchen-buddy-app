package com.example.kitchenassistant.data.auth

import com.example.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    suspend fun signIn(email: String, password: String): String {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return supabase.auth.currentAccessTokenOrNull()
            ?: error("Sign in succeeded but no access token was returned")
    }

    // signOut() clears the session from both memory AND SharedPreferences,
    // so the next app launch lands on LoginScreen.
    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun currentToken(): String? = supabase.auth.currentAccessTokenOrNull()
}