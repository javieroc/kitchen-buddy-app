package com.example.kitchenassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kitchenassistant.ui.navigation.AppRoute
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousCapsule

data class NavTab(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: AppRoute
)

val defaultNavTabs = listOf(
    NavTab("Chat",     Icons.Outlined.Chat,     Icons.Filled.Chat,     AppRoute.Chat),
    NavTab("Recipes",  Icons.Outlined.Book,     Icons.Outlined.Book,   AppRoute.Recipes),
    NavTab("Tools",    Icons.Outlined.Tune,     Icons.Outlined.Tune,   AppRoute.Tools),
    NavTab("Settings", Icons.Outlined.Settings, Icons.Filled.Settings, AppRoute.Settings),
)

@Composable
fun GlassBottomNav(
    navController: NavController,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    tabs: List<NavTab> = defaultNavTabs
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = modifier
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
                    drawRect(
                        color = Color.White.copy(alpha = 0.20f),
                        style = Stroke(width = 1.dp.toPx())
                    )
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
            tabs.forEach { tab ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.hasRoute(tab.route::class)
                } == true

                NavTabItem(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: NavTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (isSelected) Color(0xFF1FB4FF) else Color.White.copy(alpha = 0.55f)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = Color.White)
            ) { onClick() }
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