package com.example.kitchenassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kitchenassistant.R
import com.example.kitchenassistant.ui.components.GlassCard
import com.example.kitchenassistant.ui.components.GlassGradientButton
import com.example.kitchenassistant.ui.components.GlassInputField
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun LoginScreen(
    onLoginSuccess: (accessToken: String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is LoginUiState.Loading
    val errorMessage = (uiState as? LoginUiState.Error)?.message

    // Navigate as soon as we get a token — fires exactly once
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess((uiState as LoginUiState.Success).accessToken)
        }
    }

    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.kitchen_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop
        )

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
                    Text(
                        text = "Sign In",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    GlassInputField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.clearError()
                        },
                        placeholder = "Enter your email...",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        backdrop = backdrop,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassInputField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.clearError()
                        },
                        placeholder = "Enter your password...",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        backdrop = backdrop,
                        enabled = !isLoading
                    )

                    // Inline error — shown below the fields
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = onForgotPasswordClick,
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button swaps to a spinner during the network request
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        GlassGradientButton(
                            text = "Log In",
                            backdrop = backdrop,
                            onClick = { viewModel.signIn(email, password) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = onSignUpClick,
                            enabled = !isLoading
                        ) {
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
