package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun GlassCard(
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = ContinuousRoundedRectangle(20.dp)

    Box(
        modifier = modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(1.dp.toPx())
                    lens(
                        refractionHeight = 28.dp.toPx(),
                        refractionAmount = 40.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(Color.White.copy(alpha = 0.18f))
                    drawRect(
                        color = Color.White.copy(alpha = 0.45f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            )
    ) {
        content()
    }
}
