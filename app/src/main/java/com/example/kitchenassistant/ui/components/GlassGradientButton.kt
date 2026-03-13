package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun GlassGradientButton(
    text: String,
    backdrop: LayerBackdrop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = ContinuousRoundedRectangle(50.dp)

    val blueGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2640E8).copy(alpha = if (enabled) 0.55f else 0.30f),
            Color(0xFF1FB4FF).copy(alpha = if (enabled) 0.65f else 0.35f)
        )
    )
    val highlightGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (enabled) 0.30f else 0.10f),
            Color.Transparent
        )
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(4.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 20.dp.toPx(),
                        refractionAmount = 32.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(brush = blueGradient)
                    drawRect(brush = highlightGradient)
                    drawRect(
                        color = Color.White.copy(alpha = if (enabled) 0.55f else 0.20f),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White),
                enabled = enabled
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.5f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
