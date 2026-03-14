package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun GlassInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    val barShape  = ContinuousRoundedRectangle(28.dp)
    val sendShape = ContinuousRoundedRectangle(20.dp)

    val sendGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2640E8).copy(alpha = 0.65f),
            Color(0xFF1FB4FF).copy(alpha = 0.70f)
        )
    )
    val sendInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { barShape },
                effects = {
                    blur(10.dp.toPx())
                    vibrancy()
                },
                onDrawSurface = {
                    drawRect(Color.White.copy(alpha = 0.12f))
                    drawRect(
                        color = Color.White.copy(alpha = 0.30f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Ask kitchen Buddy...",
                                color = Color.White.copy(alpha = 0.50f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { sendShape },
                        effects = { blur(6.dp.toPx()); vibrancy() },
                        onDrawSurface = {
                            drawRect(brush = sendGradient)
                            drawRect(
                                color = Color.White.copy(alpha = 0.40f),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    )
                    .clickable(
                        interactionSource = sendInteraction,
                        indication = ripple(bounded = true, color = Color.White)
                    ) { onSend() }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
