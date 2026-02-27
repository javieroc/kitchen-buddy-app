package com.example.kitchenassistant.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitchenassistant.R
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle
import androidx.compose.foundation.Image

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Single shared backdrop — all glass elements refract the same background
    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {

        // Background kitchen image — this is what the glass refracts
        Image(
            painter = painterResource(id = R.drawable.kitchen_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop
        )

        // Vertically centered login card
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(backdrop = backdrop) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Sign In",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email input
                    GlassInputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter your email...",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        backdrop = backdrop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password input
                    GlassInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password...",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        backdrop = backdrop
                    )

                    // Forgot password link
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(onClick = onForgotPasswordClick) {
                            Text(
                                text = "Forgot Password?",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Blue gradient glass button
                    GlassGradientButton(
                        text = "Log In",
                        backdrop = backdrop,
                        onClick = { onLoginClick(email, password) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign up row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 14.sp
                        )
                        TextButton(onClick = onSignUpClick) {
                            Text(
                                text = "Sign Up",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Outer frosted glass card
// ---------------------------------------------------------------------------
@Composable
private fun GlassCard(
    backdrop: LayerBackdrop,
    content: @Composable () -> Unit
) {
    val shape = ContinuousRoundedRectangle(20.dp) // reduced from 32.dp

    Box(
        modifier = Modifier
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

// ---------------------------------------------------------------------------
// Glass input field with white icon
// ---------------------------------------------------------------------------
@Composable
private fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    backdrop: LayerBackdrop,
    isPassword: Boolean = false
) {
    val shape = ContinuousRoundedRectangle(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(16.dp.toPx())
                    vibrancy()
                },
                onDrawSurface = {
                    drawRect(Color.White.copy(alpha = 0.12f))
                    drawRect(
                        color = Color.White.copy(alpha = 0.25f),
                        style = Stroke(width = 0.8.dp.toPx())
                    )
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.White.copy(alpha = 0.50f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Blue gradient glass login button
// ---------------------------------------------------------------------------
@Composable
private fun GlassGradientButton(
    text: String,
    backdrop: LayerBackdrop,
    onClick: () -> Unit
) {
    val shape = ContinuousRoundedRectangle(50.dp) // full pill

    // Softer, more transparent blue gradient so the glass shows through
    val blueGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2640E8).copy(alpha = 0.55f), // indigo blue — semi-transparent
            Color(0xFF1FB4FF).copy(alpha = 0.65f)  // sky blue — semi-transparent
        )
    )

    // Separate stronger gradient just for the top specular highlight
    val highlightGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.30f), // bright highlight at top
            Color.Transparent                // fades out downward
        )
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(4.dp.toPx())            // low blur — keeps background visible
                    vibrancy()
                    lens(
                        refractionHeight = 20.dp.toPx(), // thick glass lens depth
                        refractionAmount = 32.dp.toPx(), // strong refraction for depth
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    // Semi-transparent blue tint — lets the glass show through
                    drawRect(brush = blueGradient)
                    // Top specular highlight — the key to making it look like real glass
                    drawRect(brush = highlightGradient)
                    // Bright white border for glass edge definition
                    drawRect(
                        color = Color.White.copy(alpha = 0.55f),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
