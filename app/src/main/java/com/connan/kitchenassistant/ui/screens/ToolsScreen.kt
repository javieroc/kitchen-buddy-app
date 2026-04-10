package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

private data class ToolItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ToolsScreen(
    backdrop: LayerBackdrop,
    onNavigateToConverter: () -> Unit = {}
) {
    val tools = listOf(
        ToolItem("Convert",  Icons.Outlined.Calculate, onNavigateToConverter),
        ToolItem("Calories", Icons.Outlined.Whatshot,  {}),
        ToolItem("Stock",    Icons.Outlined.Inventory, {}),
        ToolItem("Inspire",  Icons.Outlined.Help,      {}),
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolButton(tool = tools[0], backdrop = backdrop, modifier = Modifier.weight(1f))
                ToolButton(tool = tools[1], backdrop = backdrop, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolButton(tool = tools[2], backdrop = backdrop, modifier = Modifier.weight(1f))
                ToolButton(tool = tools[3], backdrop = backdrop, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ToolButton(
    tool: ToolItem,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    val shape = ContinuousRoundedRectangle(20.dp)

    val blueGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2640E8).copy(alpha = 0.65f),
            Color(0xFF1FB4FF).copy(alpha = 0.70f)
        )
    )
    val highlight = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(6.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 20.dp.toPx(),
                        refractionAmount = 28.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(brush = blueGradient)
                    drawRect(brush = highlight)
                    drawRect(
                        color = Color.White.copy(alpha = 0.40f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { tool.onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.label,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = tool.label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
