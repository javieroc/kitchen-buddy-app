package com.example.kitchenassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kitchenassistant.ui.screens.LoginScreen
import com.example.kitchenassistant.ui.theme.KitchenAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenAssistantTheme {
                LoginScreen()
            }
        }
    }
}