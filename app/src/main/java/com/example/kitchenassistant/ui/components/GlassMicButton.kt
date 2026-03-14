package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun GlassMicButton(
    backdrop: LayerBackdrop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = ContinuousRoundedRectangle(40.dp) // radius = size / 2 → circle

    val blueGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1FB4FF).copy(alpha = 0.75f),
            Color(0xFF2640E8).copy(alpha = 0.85f)
        )
    )
    val highlight = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(80.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(6.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 24.dp.toPx(),
                        refractionAmount = 36.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(brush = blueGradient)
                    drawRect(brush = highlight)
                    drawRect(
                        color = Color.White.copy(alpha = 0.50f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = Color.White)
            ) { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice input",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}
