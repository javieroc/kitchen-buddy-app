package com.example.kitchenassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kitchenassistant.data.supabase
import com.example.kitchenassistant.ui.screens.HomeScreen
import com.example.kitchenassistant.ui.screens.LoginScreen
import com.example.kitchenassistant.ui.theme.KitchenAssistantTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenAssistantTheme {
                val sessionStatus by supabase.auth.sessionStatus.collectAsState()

                when (sessionStatus) {
                    is SessionStatus.Initializing -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    is SessionStatus.Authenticated -> {
                        HomeScreen()
                    }

                    is SessionStatus.NotAuthenticated,
                    is SessionStatus.RefreshFailure -> {
                        LoginScreen()
                    }
                }
            }
        }
    }
}