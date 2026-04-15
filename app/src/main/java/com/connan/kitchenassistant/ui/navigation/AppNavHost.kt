package com.connan.kitchenassistant.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.connan.kitchenassistant.ui.screens.ConversationsScreen
import com.connan.kitchenassistant.ui.screens.ConversationsViewModel
import com.connan.kitchenassistant.ui.screens.HomeScreen
import com.connan.kitchenassistant.ui.screens.HomeViewModel
import com.connan.kitchenassistant.ui.screens.RecipeDetailScreen
import com.connan.kitchenassistant.ui.screens.RecipesScreen
import com.connan.kitchenassistant.ui.screens.RecipesViewModel
import com.connan.kitchenassistant.ui.screens.SettingsScreen
import com.connan.kitchenassistant.ui.screens.ConverterScreen
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
        composable<AppRoute.Chat> { entry ->
            val conversationsViewModel: ConversationsViewModel = viewModel(entry)

            // Refresh the list every time this screen resumes (e.g. back from a chat)
            LaunchedEffect(entry) {
                entry.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    conversationsViewModel.loadThreads()
                }
            }

            ConversationsScreen(
                backdrop = backdrop,
                viewModel = conversationsViewModel,
                onNavigateToChat = { thread ->
                    navController.navigate(
                        AppRoute.ChatDetail(
                            chatId = thread.id,
                            chatTitle = thread.title ?: "Chat"
                        )
                    )
                }
            )
        }
        composable<AppRoute.ChatDetail> { entry ->
            val chatViewModel: HomeViewModel = viewModel(entry)
            HomeScreen(backdrop = backdrop, viewModel = chatViewModel)
        }
        composable<AppRoute.Recipes> { entry ->
            val viewModel: RecipesViewModel = viewModel(entry)
            RecipesScreen(
                backdrop = backdrop,
                viewModel = viewModel,
                onNavigateToDetail = { item ->
                    navController.navigate(
                        AppRoute.RecipeDetail(
                            recipeId = item.recipe.id,
                            recipeName = item.recipe.name
                        )
                    )
                }
            )
        }
        composable<AppRoute.RecipeDetail> { entry ->
            val args = entry.toRoute<AppRoute.RecipeDetail>()
            val recipesEntry = remember(entry) {
                navController.getBackStackEntry<AppRoute.Recipes>()
            }
            val viewModel: RecipesViewModel = viewModel(recipesEntry)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val item = uiState.recipes.find { it.recipe.id == args.recipeId }
            if (item != null) {
                RecipeDetailScreen(item = item, backdrop = backdrop)
            }
        }
        composable<AppRoute.Tools> {
            ToolsScreen(
                backdrop = backdrop,
                onNavigateToConverter = { navController.navigate(AppRoute.Converter) }
            )
        }
        composable<AppRoute.Converter> {
            ConverterScreen(backdrop = backdrop)
        }
        composable<AppRoute.Settings> {
            SettingsScreen(backdrop = backdrop)
        }
    }
}