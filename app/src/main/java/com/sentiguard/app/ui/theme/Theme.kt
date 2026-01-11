package com.sentiguard.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = StatusSafe,
    onPrimary = TextPrimary,
    secondary = StatusWarning,
    onSecondary = BlackPrimary,
    tertiary = StatusDanger,
    background = BlackPrimary,
    onBackground = TextPrimary,
    surface = BlackSecondary,
    onSurface = TextPrimary,
    error = StatusDanger,
    onError = TextPrimary
)

// We default to dark scheme for Sentiguard even in light mode
// to ensure consistency and low eye strain in low-light environments (sewers).
private val LightColorScheme = DarkColorScheme.copy(
    background = BlackPrimary, // Enforce dark background
    onBackground = TextPrimary
)

@Composable
fun SentiguardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to ensure safety colors are consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // We explicitly ignore dynamicColor to preserve safety color coding semantics
        // always use dark scheme logic
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}