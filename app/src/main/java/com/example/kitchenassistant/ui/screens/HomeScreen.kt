package com.example.kitchenassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle

data class ChatMessage(val text: String, val isFromUser: Boolean)

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

    // One shared backdrop — all glass elements refract the same background
    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image — registered as backdrop source
        Image(
            painter = painterResource(id = R.drawable.kitchen_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop
        )

        // Subtle dark scrim so white text is always legible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Main layout
        Column(modifier = Modifier.fillMaxSize()) {

            // Chat messages
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

            // Mic button centred above the input bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassMicButton(backdrop = backdrop, onClick = { /* handle voice */ })
            }

            // Text input bar
            GlassInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = { inputText = "" },
                backdrop = backdrop
            )

            // Bottom navigation
            GlassBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                backdrop = backdrop
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, backdrop: LayerBackdrop) {
    val bubbleShape = ContinuousRoundedRectangle(18.dp)
    val isUser = message.isFromUser

    val tintColor   = if (isUser) Color(0xFF2640E8).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f)
    val borderColor = if (isUser) Color(0xFF1FB4FF).copy(alpha = 0.60f) else Color.White.copy(alpha = 0.28f)

    Row(
        modifier = Modifier.fillMaxWidth(),
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
            Text(text = message.text, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun GlassMicButton(backdrop: LayerBackdrop, onClick: () -> Unit) {
    val shape = ContinuousRoundedRectangle(40.dp) // radius = size / 2 = circle

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
        modifier = Modifier
            .size(80.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(6.dp.toPx())
                    vibrancy()
                    lens(
                        refractionHeight = 24.dp.toPx(),
                        refractionAmount = 36.dp.toPx(),
                        chromaticAberration = true
                    )
                },
                onDrawSurface = {
                    drawRect(brush = blueGradient)
                    drawRect(brush = highlight)
                    drawRect(color = Color.White.copy(alpha = 0.50f), style = Stroke(width = 1.5.dp.toPx()))
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = Color.White)
            ) { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice input",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun GlassInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    backdrop: LayerBackdrop
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
        modifier = Modifier
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
                    drawRect(color = Color.White.copy(alpha = 0.30f), style = Stroke(width = 1.dp.toPx()))
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
                                "Ask kitchen Buddy...",
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
                            drawRect(color = Color.White.copy(alpha = 0.40f), style = Stroke(width = 1.dp.toPx()))
                        }
                    )
                    .clickable(interactionSource = sendInteraction, indication = ripple(bounded = true, color = Color.White)) { onSend() }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private data class NavTab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

private val navTabs = listOf(
    NavTab("Chat",     Icons.Outlined.Chat,     Icons.Filled.Chat),
    NavTab("Recipes",  Icons.Outlined.Book,     Icons.Outlined.Book),
    NavTab("Tools",    Icons.Outlined.Tune,     Icons.Outlined.Tune),
    NavTab("Settings", Icons.Outlined.Settings, Icons.Filled.Settings),
)

@Composable
private fun GlassBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    backdrop: LayerBackdrop
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(68.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { ContinuousCapsule },
                effects = {
                    vibrancy()
                    blur(8.dp.toPx())
                    lens(
                        refractionHeight = 16.dp.toPx(),
                        refractionAmount = 20.dp.toPx(),
                        chromaticAberration = false
                    )
                },
                onDrawSurface = {
                    drawRect(Color.Black.copy(alpha = 0.30f))
                    drawRect(color = Color.White.copy(alpha = 0.20f), style = Stroke(width = 1.dp.toPx()))
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navTabs.forEachIndexed { index, tab ->
                NavTabItem(
                    tab = tab,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(tab: NavTab, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (isSelected) Color(0xFF1FB4FF) else Color.White.copy(alpha = 0.55f)

    Column(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = ripple(bounded = false, color = Color.White)) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.icon,
            contentDescription = tab.label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = tab.label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
