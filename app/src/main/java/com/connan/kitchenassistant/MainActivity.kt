package com.connan.kitchenassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.connan.kitchenassistant.data.supabase
import com.connan.kitchenassistant.ui.navigation.AppShell
import com.connan.kitchenassistant.ui.screens.LoginScreen
import com.connan.kitchenassistant.ui.theme.KitchenAssistantTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenAssistantTheme {
                val sessionStatus by supabase.auth.sessionStatus.collectAsState()

                // Track whether we have ever reached an authenticated state.
                // This lets us distinguish a cold start (show loading) from an
                // app-resume re-initialization (keep AppShell alive, no blink).
                var everAuthenticated by remember { mutableStateOf(false) }
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> everAuthenticated = true
                    is SessionStatus.NotAuthenticated,
                    is SessionStatus.RefreshFailure -> everAuthenticated = false
                    else -> Unit
                }

                when {
                    // Cold start: session not yet restored — show spinner
                    sessionStatus is SessionStatus.Initializing && !everAuthenticated -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    // Authenticated, or briefly re-initializing after a prior
                    // authenticated session (app resume) — keep AppShell in place
                    sessionStatus is SessionStatus.Authenticated ||
                    (sessionStatus is SessionStatus.Initializing && everAuthenticated) -> {
                        AppShell()
                    }

                    // Signed out or token refresh failed
                    else -> {
                        LoginScreen()
                    }
                }
            }
        }
    }
}
