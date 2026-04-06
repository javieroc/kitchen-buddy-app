package com.connan.kitchenassistant.data.recipes

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("created_at") val createdAt: String
)

data class RecipeIngredientDto(
    @SerializedName("id") val id: String,
    @SerializedName("recipe_id") val recipeId: String,
    @SerializedName("ingredient_id") val ingredientId: String,
    @SerializedName("quantity_used") val quantityUsed: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("ingredient_name") val ingredientName: String,
    @SerializedName("unit_of_measure") val unitOfMeasure: String,
    @SerializedName("cost_per_unit") val costPerUnit: String
)

data class RecipeWithIngredientsDto(
    @SerializedName("recipe") val recipe: RecipeDto,
    @SerializedName("ingredients") val ingredients: List<RecipeIngredientDto> = emptyList()
)

data class RecipesPageDto(
    @SerializedName("items") val items: List<RecipeWithIngredientsDto>,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("next_cursor") val nextCursor: String?
)
