package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTertiary

@Composable
fun ChillBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface.copy(alpha = 0.95f))
            .border(width = 1.dp, color = DarkBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                title = "Главная",
                selected = currentTab == AppTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                tag = "tab_home",
                onClick = { onTabSelected(AppTab.HOME) }
            )
            BottomBarItem(
                title = "Каталог",
                selected = currentTab == AppTab.CATALOG,
                selectedIcon = Icons.Filled.MenuBook,
                unselectedIcon = Icons.Outlined.MenuBook,
                tag = "tab_catalog",
                onClick = { onTabSelected(AppTab.CATALOG) }
            )
            BottomBarItem(
                title = "Читалка",
                selected = currentTab == AppTab.READER,
                selectedIcon = Icons.Filled.AutoStories,
                unselectedIcon = Icons.Outlined.AutoStories,
                tag = "tab_reader",
                isSpecial = true,
                onClick = { onTabSelected(AppTab.READER) }
            )
            BottomBarItem(
                title = "Профиль",
                selected = currentTab == AppTab.PROFILE,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                tag = "tab_profile",
                onClick = { onTabSelected(AppTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    title: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    tag: String,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = if (isSpecial) CyberGreen else NeonPurple),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(36.dp, 28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSpecial) CyberGreen.copy(alpha = 0.15f)
                            else NeonPurple.copy(alpha = 0.20f)
                        )
                )
            }
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = title,
                tint = when {
                    selected && isSpecial -> CyberGreen
                    selected -> NeonPurple
                    else -> TextTertiary
                },
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            color = when {
                selected && isSpecial -> CyberGreen
                selected -> TextPrimary
                else -> TextTertiary
            },
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
