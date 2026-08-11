package com.yeremi.ocupa2app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Ocupa2ColorScheme = darkColorScheme(
    primary = AcidLime,
    secondary = ElectricTeal,
    tertiary = SolarOrange,
    background = Obsidian,
    surface = Obsidian,
    surfaceVariant = GraphiteRaise,
    surfaceContainer = Graphite,
    onPrimary = Obsidian,
    onBackground = IceWhite,
    onSurface = IceWhite,
    onSurfaceVariant = MutedGray,
    outline = Border,
    outlineVariant = BorderStrong
)

@Composable
fun Ocupa2AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = Ocupa2ColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
