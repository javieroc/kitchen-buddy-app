package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connan.kitchenassistant.BuildConfig
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun SettingsScreen(
    backdrop: LayerBackdrop,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Profile card ──────────────────────────────────────────────────
        SettingsCard(backdrop = backdrop) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Avatar with user initial
                val initial = uiState.email.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2640E8), Color(0xFF1FB4FF))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "ACCOUNT",
                        color = Color(0xFF1FB4FF).copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = uiState.email.ifEmpty { "—" },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Actions card ──────────────────────────────────────────────────
        SettingsCard(backdrop = backdrop) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.10f),
                        thickness = 0.5.dp
                    )
                }

                SettingsRow(
                    label = "Sign Out",
                    labelColor = Color(0xFFFF6B6B),
                    isLoading = uiState.isSigningOut,
                    onClick = { viewModel.signOut() },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // ── App version ───────────────────────────────────────────────────
        Text(
            text = "Kitchen Assistant  ·  v${BuildConfig.VERSION_NAME}",
            color = Color.White.copy(alpha = 0.30f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ── reusable glass card ───────────────────────────────────────────────────────

@Composable
private fun SettingsCard(
    backdrop: LayerBackdrop,
    content: @Composable () -> Unit
) {
    val shape = ContinuousRoundedRectangle(20.dp)
    Box(
        modifier = Modifier
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
                        color = Color.White.copy(alpha = 0.22f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            )
    ) {
        content()
    }
}

// ── settings row ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsRow(
    label: String,
    labelColor: Color = Color.White,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White),
                enabled = !isLoading
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = labelColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            leadingContent?.invoke()
        }

        Text(
            text = label,
            color = if (isLoading) labelColor.copy(alpha = 0.5f) else labelColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
