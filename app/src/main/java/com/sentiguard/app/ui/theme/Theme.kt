package com.sentiguard.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light Theme for SafeGuard
private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = TextLight,
    primaryContainer = RedLight,
    onPrimaryContainer = RedDark,

    secondary = GreenSafe,
    onSecondary = TextLight,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenSafe,
    
    background = BackgroundWhite,
    onBackground = TextDark,
    
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceGrey,
    onSurfaceVariant = TextGrey,

    error = RedPrimary,
    onError = TextLight
)

@Composable
fun SentiguardTheme(
    darkTheme: Boolean = false, // Force Light Theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
