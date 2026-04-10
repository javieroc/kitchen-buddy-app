package com.connan.kitchenassistant.data.recipes

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RecipeApiService {

    @GET("recipes")
    suspend fun listRecipes(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int,
        @Query("before") cursor: String?
    ): RecipesPageDto
}
