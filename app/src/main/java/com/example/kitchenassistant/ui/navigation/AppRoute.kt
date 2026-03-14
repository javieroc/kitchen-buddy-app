package com.example.kitchenassistant.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Chat : AppRoute

    @Serializable
    data object Recipes : AppRoute

    @Serializable
    data object Tools : AppRoute

    @Serializable
    data object Settings : AppRoute
}