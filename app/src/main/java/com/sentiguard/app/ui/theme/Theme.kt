package com.sentiguard.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Forced Dark Scheme for Safety Critical Low-Light Usage
private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceGood,
    onPrimaryContainer = BrandAccent,

    secondary = BrandSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = BlackTertiary,
    onSecondaryContainer = TextSecondary,

    tertiary = BrandAccent,
    onTertiary = BlackPrimary,

    background = BlackPrimary,
    onBackground = TextPrimary,

    surface = BlackSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BlackTertiary,
    onSurfaceVariant = TextSecondary,

    error = StatusDanger,
    onError = TextPrimary,
    errorContainer = SurfaceDanger,
    onErrorContainer = StatusDanger
)

@Composable
fun SentiguardTheme(
    darkTheme: Boolean = true, // Force dark theme always as per safety reqs
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to ensure safety colors are consistent
    content: @Composable () -> Unit
) {
    // We ignore toggle and system setting to enforce safety-first dark UI
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}