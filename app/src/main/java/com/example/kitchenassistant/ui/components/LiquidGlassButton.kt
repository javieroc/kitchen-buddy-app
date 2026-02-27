package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun LiquidGlassButton(
    icon: ImageVector,
    onClick: () -> Unit,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    val circleShape = ContinuousRoundedRectangle(28.dp)

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { circleShape },
                effects = {
                    vibrancy()
                    blur(30.dp.toPx())
                    lens(
                        refractionHeight = 10.dp.toPx(),
                        refractionAmount = 18.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    // Frosted white tint
                    drawRect(Color.White.copy(alpha = 0.15f))
                    // Subtle glass border
                    drawRect(
                        color = Color.White.copy(alpha = 0.35f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = Color.White)
            ) { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}