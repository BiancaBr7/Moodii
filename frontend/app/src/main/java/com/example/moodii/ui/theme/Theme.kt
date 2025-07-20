package com.example.moodii.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // Only import Color class, not individual colors here

// --- Color Schemes ---
// These schemes now reference the colors defined in Color.kt
private val LightColorScheme = lightColorScheme(
    primary = AudioRecorderButtonDefault, // Primary interactive elements like buttons
    onPrimary = AudioRecorderWhiteCircle, // Text/icons on primary
    primaryContainer = AudioRecorderButtonHover, // A lighter shade for containers/hover states
    onPrimaryContainer = AudioRecorderTextPrimary,

    secondary = AudioRecorderMoodSection, // Secondary interactive elements or distinct sections
    onSecondary = AudioRecorderTextPrimary,
    secondaryContainer = AudioRecorderMoodSection.copy(alpha = 0.8f), // A slightly lighter secondary container
    onSecondaryContainer = AudioRecorderTextPrimary,

    background = AudioRecorderBackgroundPage, // Main page background
    onBackground = AudioRecorderTextPrimary, // Default text color on background

    surface = AudioRecorderContainerBg, // Main card/container background
    onSurface = AudioRecorderTextPrimary, // Default text color on surface

    error = AudioRecorderAlertError, // Error messages
    onError = AudioRecorderWhiteCircle,
    errorContainer = AudioRecorderAlertError.copy(alpha = 0.2f),
    onErrorContainer = AudioRecorderAlertError,

    outline = AudioRecorderBorderThick, // For borders, outlines

    // You can also map other colors that might be available from MaterialTheme.colorScheme
    // For example, if you want specific colors for success/info alerts via theme:
    // tertiary = AudioRecorderAlertSuccess,
    // onTertiary = AudioRecorderWhiteCircle,
    // tertiaryContainer = AudioRecorderAlertInfo,
    // onTertiaryContainer = AudioRecorderWhiteCircle,
)

private val DarkColorScheme = darkColorScheme(
    // A basic dark theme adaptation using your pixelated colors.
    // You'll likely want to fine-tune these for actual dark mode usage.
    primary = AudioRecorderButtonActive,
    onPrimary = AudioRecorderWhiteCircle,
    primaryContainer = AudioRecorderShadowGeneral,
    onPrimaryContainer = AudioRecorderWhiteCircle,

    secondary = AudioRecorderMoodSection.copy(alpha = 0.5f),
    onSecondary = AudioRecorderWhiteCircle,
    secondaryContainer = AudioRecorderMoodSection.copy(alpha = 0.3f),
    onSecondaryContainer = AudioRecorderWhiteCircle,

    background = AudioRecorderShadowGeneral, // Darker background
    onBackground = AudioRecorderWhiteCircle,

    surface = AudioRecorderBorderThick, // Darker container surface
    onSurface = AudioRecorderWhiteCircle,

    error = AudioRecorderAlertError,
    onError = AudioRecorderWhiteCircle,

    outline = AudioRecorderButtonDefault,
)

@Composable
fun PixelatedAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Use the Typography object defined in Type.kt
        content = content
    )
}