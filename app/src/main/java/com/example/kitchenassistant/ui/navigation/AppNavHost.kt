package com.example.kitchenassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kitchenassistant.ui.screens.HomeScreen
import com.example.kitchenassistant.ui.screens.RecipesScreen
import com.example.kitchenassistant.ui.screens.SettingsScreen
import com.example.kitchenassistant.ui.screens.ToolsScreen
import com.kyant.backdrop.backdrops.LayerBackdrop

@Composable
fun AppNavHost(
    navController: NavHostController,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Chat,
        modifier = modifier
    ) {
        composable<AppRoute.Chat> {
            HomeScreen(backdrop = backdrop)
        }
        composable<AppRoute.Recipes> {
            RecipesScreen()
        }
        composable<AppRoute.Tools> {
            ToolsScreen()
        }
        composable<AppRoute.Settings> {
            SettingsScreen()
        }
    }
}