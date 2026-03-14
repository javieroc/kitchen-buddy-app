package com.example.kitchenassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kitchenassistant.R
import com.example.kitchenassistant.ui.components.ChatBubble
import com.example.kitchenassistant.ui.components.ChatMessage
import com.example.kitchenassistant.ui.components.GlassBottomNav
import com.example.kitchenassistant.ui.components.GlassInputBar
import com.example.kitchenassistant.ui.components.GlassMicButton
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

private val sampleMessages = listOf(
    ChatMessage("Hi! I'm your kitchen assistant. What's on the menu? I can help you find a recipe or convert measurements.", false),
    ChatMessage("Can you suggest a quick pasta recipe?", true),
    ChatMessage("Sure! Try spaghetti aglio e olio — garlic, olive oil, chilli flakes, parsley. Ready in 20 minutes.", false),
    ChatMessage("What about a vegetarian option?", true),
    ChatMessage("Mushroom risotto! I can walk you through it step by step if you like.", false),
)

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

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
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 56.dp,
                    bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sampleMessages) { message ->
                    ChatBubble(message = message, backdrop = backdrop)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassMicButton(
                    backdrop = backdrop,
                    onClick = { /* handle voice */ }
                )
            }

            GlassInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = { inputText = "" },
                backdrop = backdrop
            )

            GlassBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                backdrop = backdrop
            )
        }
    }
}
