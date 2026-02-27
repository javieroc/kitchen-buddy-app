package com.example.kitchenassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitchenassistant.R
import com.example.kitchenassistant.ui.components.LiquidGlassButton
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun HomeScreen() {
    // Shared backdrop state — connects the background source to the glass button
    val backdrop = rememberLayerBackdrop()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background image — registered as the backdrop source
            Image(
                painter = painterResource(id = R.drawable.kitchen_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop), // captures this layer for the glass effect
                contentScale = ContentScale.Crop
            )

            // Text overlay
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Kitchen Assistant",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome back!",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Circular liquid glass FAB — bottom-right corner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                LiquidGlassButton(
                    icon = Icons.Default.Add,
                    onClick = { /* Handle click */ },
                    backdrop = backdrop
                )
            }
        }
    }
}
