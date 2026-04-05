package com.connan.kitchenassistant.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connan.kitchenassistant.data.recipes.RecipeRepository
import com.connan.kitchenassistant.data.recipes.RecipeWithIngredientsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipesUiState(
    val recipes: List<RecipeWithIngredientsDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class RecipesViewModel(
    private val repository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recipes = repository.listRecipes()
                _uiState.update { it.copy(recipes = recipes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Could not load recipes.") }
            }
        }
    }
}
