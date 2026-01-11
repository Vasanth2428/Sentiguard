package com.sentiguard.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sentiguard Typography Hierarchy
// 1. Status: Largest, Bold (mapped to displayLarge)
// 2. Title: Medium, Bold (mapped to headlineMedium)
// 3. Supporting: Small, Regular (mapped to bodyLarge)

val Typography = Typography(
    // STATUS
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp, // Large enough for ease of reading
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    // TITLE / HEADERS
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold, // SemiBold is more professional than Bold for UI headers
        fontSize = 20.sp, // Reduced slightly to look more refined
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // SUPPORTING / BODY
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp // Standard reading tracking
    ),
    // BUTTON TEXT
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium, // Medium looks sharper on buttons than Bold
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp // Tighter button text
    )
)