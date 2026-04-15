package com.connan.kitchenassistant.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Chat : AppRoute

    @Serializable
    data class ChatDetail(val chatId: String, val chatTitle: String) : AppRoute

    @Serializable
    data object Recipes : AppRoute

    @Serializable
    data class RecipeDetail(val recipeId: String, val recipeName: String) : AppRoute

    @Serializable
    data object Tools : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data object Converter : AppRoute
}