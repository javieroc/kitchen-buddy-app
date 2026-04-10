package com.connan.kitchenassistant.data.recipes

import com.connan.kitchenassistant.data.chat.RetrofitClient
import com.connan.kitchenassistant.data.supabase
import io.github.jan.supabase.auth.auth

class RecipeRepository {
    private val api = RetrofitClient.recipeApiService

    private suspend fun bearerToken(): String {
        val token = supabase.auth.currentAccessTokenOrNull()
            ?: error("No active session — user must be logged in")
        return "Bearer $token"
    }

    suspend fun listRecipes(cursor: String? = null, limit: Int = 20): RecipesPageDto {
        return api.listRecipes(bearerToken(), limit = limit, cursor = cursor)
    }
}
