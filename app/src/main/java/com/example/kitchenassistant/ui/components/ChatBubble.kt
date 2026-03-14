package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

data class ChatMessage(val text: String, val isFromUser: Boolean)

@Composable
fun ChatBubble(
    message: ChatMessage,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    val bubbleShape = ContinuousRoundedRectangle(18.dp)
    val isUser = message.isFromUser

    val tintColor   = if (isUser) Color(0xFF2640E8).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f)
    val borderColor = if (isUser) Color(0xFF1FB4FF).copy(alpha = 0.60f) else Color.White.copy(alpha = 0.28f)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { bubbleShape },
                    effects = {
                        blur(8.dp.toPx())
                        vibrancy()
                        lens(
                            refractionHeight = 10.dp.toPx(),
                            refractionAmount = 14.dp.toPx(),
                            chromaticAberration = isUser
                        )
                    },
                    onDrawSurface = {
                        drawRect(tintColor)
                        drawRect(color = borderColor, style = Stroke(width = 1.dp.toPx()))
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
