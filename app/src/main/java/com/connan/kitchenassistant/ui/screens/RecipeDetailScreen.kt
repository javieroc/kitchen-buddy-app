package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.connan.kitchenassistant.data.recipes.RecipeIngredientDto
import com.connan.kitchenassistant.data.recipes.RecipeWithIngredientsDto
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun RecipeDetailScreen(
    item: RecipeWithIngredientsDto,
    backdrop: LayerBackdrop
) {
    val recipe = item.recipe
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero image
        if (!recipe.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            )
            Spacer(Modifier.height(20.dp))
        } else {
            Spacer(Modifier.height(8.dp))
        }

        // Header card
        val cardShape = ContinuousRoundedRectangle(18.dp)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { cardShape },
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
            if (!recipe.category.isNullOrBlank()) {
                Text(
                    text = recipe.category.uppercase(),
                    color = Color(0xFF1FB4FF).copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            Text(
                text = recipe.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )

            if (!recipe.description.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = recipe.description,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YIELD",
                    color = Color(0xFF1FB4FF).copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "${recipe.yieldAmount} ${recipe.yieldUnit}",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }
        }

        // Ingredients section
        if (item.ingredients.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { cardShape },
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Ingredients",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))

                item.ingredients.forEachIndexed { index, ingredient ->
                    IngredientRow(ingredient = ingredient)
                    if (index < item.ingredients.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun IngredientRow(ingredient: RecipeIngredientDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.ingredientName,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "${ingredient.quantityUsed} ${ingredient.unitOfMeasure}",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 13.sp
        )
    }
}
