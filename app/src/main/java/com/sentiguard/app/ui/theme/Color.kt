package com.sentiguard.app.ui.theme

import androidx.compose.ui.graphics.Color

// SafeGuard Brand Colors
val RedPrimary = Color(0xFFE53935) // Safety Red
val RedDark = Color(0xFFC62828)
val RedLight = Color(0xFFFFEBEE) // For backgrounds of critical items

val GreenSafe = Color(0xFF2E7D32) // Forest Green
val GreenLight = Color(0xFFE8F5E9)

val AmberWarning = Color(0xFFFFC107)
val AmberLight = Color(0xFFFFF8E1)
val AmberDark = Color(0xFFFFA000)

// Backgrounds & Surfaces (Light Mode)
val BackgroundWhite = Color(0xFFFAFAFA) // Very light grey/white
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceGrey = Color(0xFFF5F5F5)

// Typography
val TextDark = Color(0xFF212121)
val TextGrey = Color(0xFF757575)
val TextLight = Color(0xFFFFFFFF) // On Red background

// UI Elements
val DividerColor = Color(0xFFEEEEEE)
val ShadowColor = Color(0xFF000000)

// Status Mappings
val StatusSafeBg = GreenLight
val StatusSafeText = GreenSafe

val StatusWarningBg = AmberLight
val StatusWarningText = Color(0xFFFFA000) // Darker amber for text

val StatusCriticalBg = RedLight
val StatusCriticalText = RedDark
