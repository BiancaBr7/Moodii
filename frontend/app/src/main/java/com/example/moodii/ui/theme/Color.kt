package com.example.moodii.ui.theme

import androidx.compose.ui.graphics.Color

// --- Original Theme Colors (from your previous Theme.kt) ---
val PixelatedPurpleLight = Color(0xFFD2BBF1) // body background
val PixelatedPurpleMedium = Color(0xFFE5D3F8) // lighter pastel purple for containers
val PixelatedPurpleDark = Color(0xFFA78BFA) // thicker border, progress fill
val PixelatedPurpleDarker = Color(0xFF5A3D7A) // darkest purple for borders/shadows/text
val PixelatedPurpleAccent = Color(0xFFC084FC) // shadow color
val PixelatedPurpleLid = Color(0xFFA78AD0) // trash can lid
val PixelatedPurpleFill = Color(0xFFC2A7E0) // trash can body
val PixelatedPurpleTrack = Color(0xFFD8B4FE) // progress track
val PixelatedPurpleText = Color(0xFF6B21A8) // text color (for controls, etc.)
val PixelatedWhite = Color(0xFFF0F0F0) // fake minimize/maximize buttons

// Specific colors for Login/SignUp from your original code, mapped to theme
val LightLavender = Color(0xFFE6E6FA)
val FloralWhite = Color(0xFFFFF0F5)
val DarkViolet = Color(0xFF8A2BE2)
val MediumPurple = Color(0xFF9370DB)
val Indigo = Color(0xFF4B0082)
val Orchid = Color(0xFFDA70D6)


// --- New Colors from HTML Conversion (integrated and named for clarity) ---
val AudioRecorderBackgroundPage = Color(0xFFE0B0FF) // From HTML body background
val AudioRecorderContainerBg = Color(0xFFF0E6FA) // From HTML .tab-container bg
val AudioRecorderBorderThick = Color(0xFF8E44AD) // From HTML .pixel-border
val AudioRecorderShadowGeneral = Color(0xFF6C3483) // From HTML box-shadow & button border/shadow
val AudioRecorderButtonDefault = Color(0xFFBB8FCE) // From HTML .pixel-button background
val AudioRecorderButtonHover = Color(0xFFA569BD) // From HTML .pixel-button:hover
val AudioRecorderButtonActive = Color(0xFF8E44AD) // From HTML .pixel-button:active
val AudioRecorderRecordButton = Color(0xFF8E44AD) // Specific Record Button Bg (matches active)
val AudioRecorderMoodSection = Color(0xFFDAB8F0) // From HTML Predicted Mood Section background
val AudioRecorderWhiteCircle = Color(0xFFFFFFFF) // White circle in record button

// Text/UI colors specific to the Audio Recorder design
val AudioRecorderTextPrimary = Color(0xFF6C3483) // Used for titles, some text (matches shadow)
val AudioRecorderRed = Color(0xFFFF0000) // For recording indicator (original HTML was red-500)

// Alert Colors (from HTML)
val AudioRecorderAlertSuccess = Color(0xFF4CAF50)
val AudioRecorderAlertError = Color(0xFFF44336)
val AudioRecorderAlertInfo = Color(0xFF2196F3)