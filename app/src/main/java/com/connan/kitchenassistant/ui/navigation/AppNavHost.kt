package com.connan.kitchenassistant.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.connan.kitchenassistant.ui.screens.HomeScreen
import com.connan.kitchenassistant.ui.screens.RecipesScreen
import com.connan.kitchenassistant.ui.screens.SettingsScreen
import com.connan.kitchenassistant.ui.screens.ToolsScreen
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
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
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