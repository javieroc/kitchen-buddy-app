package com.connan.kitchenassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connan.kitchenassistant.ui.components.ChatBubble
import com.connan.kitchenassistant.ui.components.GlassInputBar
import com.kyant.backdrop.backdrops.LayerBackdrop

@Composable
fun HomeScreen(
    backdrop: LayerBackdrop,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // With reverseLayout=true, item 0 is at the visual bottom (newest message).
    // The list starts there naturally — no explicit "scroll to bottom" needed on init.
    // Only emit scrollToBottom when the user sends a message while scrolled up.
    LaunchedEffect(Unit) {
        viewModel.scrollToBottom.collect {
            listState.animateScrollToItem(0)
        }
    }

    // Load older messages when the user scrolls to the visual top (oldest visible item).
    // hasScrolledUp guards against firing on initial render before any scroll has occurred.
    var hasScrolledUp by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex > 0) hasScrolledUp = true
    }

    val atVisualTop by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= info.totalItemsCount - 1 && info.totalItemsCount > 0
        }
    }

    LaunchedEffect(atVisualTop) {
        if (atVisualTop && hasScrolledUp && uiState.hasMoreMessages) {
            hasScrolledUp = false
            viewModel.loadMore()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            uiState.isInitializing -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.White.copy(alpha = 0.6f),
                        strokeWidth = 2.dp
                    )
                }
            }

            uiState.messages.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kitchen Buddy",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ask me anything about recipes, ingredients, costs, or substitutions.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 140.dp,  // top/bottom swapped because of reverseLayout
                        bottom = 56.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Declared first → appears at the visual bottom (typing indicator)
                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White.copy(alpha = 0.6f),
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    // index 0 = newest (already reversed in ViewModel) → visual bottom with reverseLayout=true
                    items(uiState.messages, key = { it.id }) { message ->
                        ChatBubble(message = message, backdrop = backdrop)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            GlassInputBar(
                value = inputText,
                onValueChange = {
                    inputText = it
                    viewModel.clearError()
                },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                backdrop = backdrop
            )
        }
    }
}
