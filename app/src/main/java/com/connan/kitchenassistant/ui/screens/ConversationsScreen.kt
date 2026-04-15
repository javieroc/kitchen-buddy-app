package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connan.kitchenassistant.data.chat.ChatThread
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ConversationsScreen(
    backdrop: LayerBackdrop,
    onNavigateToChat: (ChatThread) -> Unit,
    viewModel: ConversationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToChat.collect { thread ->
            onNavigateToChat(thread)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.threads.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.White.copy(alpha = 0.6f),
                        strokeWidth = 2.dp
                    )
                }
            }

            uiState.threads.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No conversations yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to start chatting with your kitchen buddy.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.threads, key = { it.id }) { thread ->
                        ConversationItem(
                            thread = thread,
                            backdrop = backdrop,
                            onClick = { viewModel.onThreadClick(thread) }
                        )
                    }
                }
            }
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = Color(0xFFFF6B6B),
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 96.dp)
            )
        }

        NewConversationButton(
            backdrop = backdrop,
            isCreating = uiState.isCreating,
            onClick = { viewModel.createNewThread() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 36.dp)
        )
    }
}

@Composable
private fun ConversationItem(
    thread: ChatThread,
    backdrop: LayerBackdrop,
    onClick: () -> Unit
) {
    val shape = ContinuousRoundedRectangle(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(1.dp.toPx())
                    lens(
                        refractionHeight = 20.dp.toPx(),
                        refractionAmount = 30.dp.toPx(),
                        chromaticAberration = false
                    )
                },
                onDrawSurface = {
                    drawRect(Color(0xFF1FB4FF).copy(alpha = 0.12f))
                    drawRect(
                        color = Color(0xFF1FB4FF).copy(alpha = 0.40f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { ContinuousRoundedRectangle(12.dp) },
                        effects = {
                            vibrancy()
                            blur(4.dp.toPx())
                        },
                        onDrawSurface = {
                            drawRect(Color.White.copy(alpha = 0.20f))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Chat,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.title ?: "New conversation",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatThreadDate(thread.lastMessageAt),
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun NewConversationButton(
    backdrop: LayerBackdrop,
    isCreating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = ContinuousRoundedRectangle(28.dp)
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
            .size(56.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(6.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 20.dp.toPx(),
                        refractionAmount = 30.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(brush = blueGradient)
                    drawRect(brush = highlight)
                    drawRect(
                        color = Color.White.copy(alpha = 0.45f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = Color.White)
            ) { onClick() }
    ) {
        if (isCreating) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New conversation",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

private fun formatThreadDate(isoDate: String): String {
    return try {
        val date = OffsetDateTime.parse(isoDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val today = LocalDate.now()
        val dateDay = date.toLocalDate()
        when {
            dateDay == today -> date.format(DateTimeFormatter.ofPattern("HH:mm"))
            dateDay == today.minusDays(1) -> "Yesterday"
            dateDay.year == today.year -> date.format(DateTimeFormatter.ofPattern("MMM d"))
            else -> date.format(DateTimeFormatter.ofPattern("MM/dd/yy"))
        }
    } catch (_: Exception) {
        ""
    }
}
