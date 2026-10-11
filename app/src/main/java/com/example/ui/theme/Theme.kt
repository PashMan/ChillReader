package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ChillReaderColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = OledBlack,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = NeonPurple,
    secondary = CyberGreen,
    onSecondary = OledBlack,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = CyberGreen,
    tertiary = CyberCyan,
    onTertiary = OledBlack,
    background = OledBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkDivider
)

@Composable
fun ChillReaderTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = OledBlack.toArgb()
                window.navigationBarColor = OledBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ChillReaderColorScheme,
        typography = Typography,
        content = content
    )
}
