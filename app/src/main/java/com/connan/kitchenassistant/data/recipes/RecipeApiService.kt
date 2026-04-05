package com.connan.kitchenassistant.data.recipes

import retrofit2.http.GET
import retrofit2.http.Header

interface RecipeApiService {

    @GET("recipes")
    suspend fun listRecipes(
        @Header("Authorization") token: String
    ): List<RecipeWithIngredientsDto>
}
