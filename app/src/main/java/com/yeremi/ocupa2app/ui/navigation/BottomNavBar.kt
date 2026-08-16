package com.yeremi.ocupa2app.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yeremi.ocupa2app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val accentColor: Color = AcidLime
)

@Composable
fun Ocupa2BottomBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {

        // Gradiente detrás de la barra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Obsidian
                        ),
                        startY = 0f,
                        endY = 40f
                    )
                )
        )

        // Barra principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 10.dp
                )
                .clip(
                    RoundedCornerShape(24.dp)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GraphiteRaise.copy(
                                alpha = 0.97f
                            ),
                            Graphite.copy(
                                alpha = 0.97f
                            )
                        )
                    )
                )
        ) {

            // Línea decorativa superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                LimeBorder,
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 3.dp,
                        vertical = 7.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                items.forEach { item ->

                    val isSelected =
                        currentRoute == item.route

                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        BottomNavItemView(
                            item = item,
                            isSelected = isSelected,
                            onClick = {

                                if (!isSelected) {

                                    navController.navigate(
                                        item.route
                                    ) {

                                        popUpTo(
                                            items.first().route
                                        ) {
                                            saveState = true
                                        }

                                        launchSingleTop = true

                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue =
            if (isSelected) 1.06f
            else 1f,
        animationSpec =
            tween(
                durationMillis = 200
            ),
        label =
            "scale_${item.route}"
    )

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(
                vertical = 5.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(3.dp)
    ) {

        // Ícono
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    if (isSelected) {

                        Brush.linearGradient(
                            colors = listOf(
                                LimeSoft,
                                LimeBorder
                            )
                        )

                    } else {

                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    }
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    if (isSelected) {
                        item.selectedIcon
                    } else {
                        item.icon
                    },
                contentDescription =
                    item.label,
                tint =
                    if (isSelected) {
                        item.accentColor
                    } else {
                        MutedGray
                    },
                modifier =
                    Modifier.size(20.dp)
            )
        }

        // Texto
        Text(
            text = item.label,
            fontSize = 9.sp,
            fontWeight =
                if (isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            color =
                if (isSelected) {
                    item.accentColor
                } else {
                    MutedGray
                },
            fontFamily =
                WorkSansFamily,
            maxLines = 1,
            overflow =
                TextOverflow.Clip
        )
    }
}