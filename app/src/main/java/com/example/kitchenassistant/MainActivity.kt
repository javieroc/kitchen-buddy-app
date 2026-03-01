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
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    HomeScreen()
                } else {
                    LoginScreen(
                        onLoginClick = { _, _ -> isLoggedIn = true }
                    )
                }
            }
        }
    }
}