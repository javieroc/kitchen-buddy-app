package com.connan.kitchenassistant.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.connan.kitchenassistant.R
import com.connan.kitchenassistant.ui.components.GlassBottomNav
import com.connan.kitchenassistant.ui.components.GlassTopBar
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun AppShell() {
    val navController = rememberNavController()
    val backdrop = rememberLayerBackdrop()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = navController.previousBackStackEntry != null

    val buddyName = remember {
        listOf(
            "Mr. Burrito",
            "Cheffy",
            "Don Steak",
            "Chuby Bobby",
            "Señor Taco",
            "Grill Master G",
            "Chef Noodle"
        ).random()
    }

    val title = when {
        navBackStackEntry?.destination?.hasRoute(AppRoute.Recipes::class) == true -> "Recipes"
        navBackStackEntry?.destination?.hasRoute(AppRoute.Tools::class) == true -> "Tools"
        navBackStackEntry?.destination?.hasRoute(AppRoute.Settings::class) == true -> "Settings"
        else -> buddyName
    }

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
            topBar = {
                GlassTopBar(
                    title = title,
                    backdrop = backdrop,
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = { navController.navigateUp() }
                )
            },
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