package com.example.kitchenassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.kitchenassistant.ui.screens.HomeScreen
import com.example.kitchenassistant.ui.screens.LoginScreen
import com.example.kitchenassistant.ui.theme.KitchenAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenAssistantTheme {
                var accessToken by remember { mutableStateOf<String?>(null) }

                if (accessToken != null) {
                    HomeScreen()
                } else {
                    LoginScreen(
                        onLoginSuccess = { token ->
                            accessToken = token
                            // token is a Supabase JWT — attach it as
                            // Authorization: Bearer <token> on your backend requests
                        }
                    )
                }
            }
        }
    }
}