package com.example.kitchenassistant.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.example.kitchenassistant.R
import com.example.kitchenassistant.ui.components.GlassBottomNav
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun AppShell() {
    val navController = rememberNavController()
    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {

        // Background — registered as backdrop source for all glass elements
        Image(
            painter = painterResource(id = R.drawable.kitchen_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Scaffold(
            // Scaffold background must be transparent so our
            // kitchen_bg image behind it shows through
            containerColor = Color.Transparent,
            bottomBar = {
                GlassBottomNav(
                    navController = navController,
                    backdrop = backdrop
                )
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                backdrop = backdrop,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}