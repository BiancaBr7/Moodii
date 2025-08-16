package com.example.moodii.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moodii.R // Make sure this import points to your R file

// Define your custom font family
// Ensure 'press_start_2p_regular.ttf' is in app/src/main/res/font/
val PressStart2P = FontFamily(
    Font(R.font.press_start_2p, FontWeight.Normal)
)

// Set of Material typography styles
// We'll use this as a base and then copy with our custom font.
private val defaultAppTypography = Typography()

val Typography = Typography(
    // Copy all default Material 3 text styles and apply PressStart2P
    displayLarge = defaultAppTypography.displayLarge.copy(fontFamily = PressStart2P),
    displayMedium = defaultAppTypography.displayMedium.copy(fontFamily = PressStart2P),
    displaySmall = defaultAppTypography.displaySmall.copy(fontFamily = PressStart2P),
    headlineLarge = defaultAppTypography.headlineLarge.copy(fontFamily = PressStart2P),
    headlineMedium = defaultAppTypography.headlineMedium.copy(fontFamily = PressStart2P),
    headlineSmall = defaultAppTypography.headlineSmall.copy(fontFamily = PressStart2P),
    titleLarge = defaultAppTypography.titleLarge.copy(fontFamily = PressStart2P),
    titleMedium = defaultAppTypography.titleMedium.copy(fontFamily = PressStart2P),
    titleSmall = defaultAppTypography.titleSmall.copy(fontFamily = PressStart2P),
    bodyLarge = defaultAppTypography.bodyLarge.copy(fontFamily = PressStart2P),
    bodyMedium = defaultAppTypography.bodyMedium.copy(fontFamily = PressStart2P),
    bodySmall = defaultAppTypography.bodySmall.copy(fontFamily = PressStart2P),
    labelLarge = defaultAppTypography.labelLarge.copy(fontFamily = PressStart2P),
    labelMedium = defaultAppTypography.labelMedium.copy(fontFamily = PressStart2P),
    labelSmall = defaultAppTypography.labelSmall.copy(fontFamily = PressStart2P),
)