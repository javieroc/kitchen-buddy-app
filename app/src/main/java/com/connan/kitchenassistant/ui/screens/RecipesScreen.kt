package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connan.kitchenassistant.data.recipes.RecipeIngredientDto
import com.connan.kitchenassistant.data.recipes.RecipeWithIngredientsDto
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun RecipesScreen(
    backdrop: LayerBackdrop,
    viewModel: RecipesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error!!,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { viewModel.loadRecipes() }) {
                        Text("Retry", color = Color(0xFF1FB4FF))
                    }
                }
            }

            uiState.recipes.isEmpty() -> {
                Text(
                    text = "No recipes yet.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.recipes, key = { it.recipe.id }) { item ->
                        RecipeCard(item = item, backdrop = backdrop)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    item: RecipeWithIngredientsDto,
    backdrop: LayerBackdrop
) {
    val shape = ContinuousRoundedRectangle(18.dp)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(8.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 10.dp.toPx(),
                        refractionAmount = 14.dp.toPx(),
                        chromaticAberration = false
                    )
                },
                onDrawSurface = {
                    drawRect(Color.Black.copy(alpha = 0.45f))
                    drawRect(
                        color = Color.White.copy(alpha = 0.28f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = item.recipe.name,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (!item.recipe.description.isNullOrBlank()) {
            Text(
                text = item.recipe.description,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        item.ingredients.forEach { ingredient ->
            IngredientBadge(ingredient)
        }
    }
}

@Composable
private fun IngredientBadge(ingredient: RecipeIngredientDto) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF1FB4FF).copy(alpha = 0.20f),
        contentColor = Color.White
    ) {
        Text(
            text = "${ingredient.quantityUsed} ${ingredient.unitOfMeasure} · ${ingredient.ingredientName}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
