package com.example.moodii.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.theme.* // Import all custom colors and theme components

// Extension function for pixelated font sizing (from previous discussions)
@Composable
fun Float.em(): androidx.compose.ui.unit.TextUnit {
    val density = LocalDensity.current
    return (this * density.fontScale).sp
}

@Composable
fun AudioRecorderScreen(navController: NavHostController) {
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var predictedMood by remember { mutableStateOf<String?>(null) }
    var alertMessageState by remember { mutableStateOf<Pair<String, Color>?>(null) } // Pair of message and background color

    // This effect handles the auto-dismissal of the alert message
    LaunchedEffect(alertMessageState) {
        if (alertMessageState != null) {
            kotlinx.coroutines.delay(3000) // 3 seconds
            alertMessageState = null
        }
    }

    // --- Helper Functions ---
    val highlightRandomMood = {
        val moods = listOf("happy", "sad", "mad", "surprised", "neutral")
        val randomIndex = (moods.indices).random()
        predictedMood = moods[randomIndex]
        println("Predicted Mood: ${predictedMood}") // For console logging
    }

    val showAlert = { message: String, type: String ->
        val bgColor = when (type) {
            "Success" -> AudioRecorderAlertSuccess // Corrected name
            "Error" -> AudioRecorderAlertError   // Corrected name
            else -> AudioRecorderAlertInfo       // Corrected name
        }
        alertMessageState = Pair(message, bgColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AudioRecorderBackgroundPage) // Corrected background color to match HTML's light purple
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp) // Max width for larger screens
                .background(
                    AudioRecorderContainerBg, // bg-[#f0e6fa]
                    shape = RoundedCornerShape(12.dp)
                )
                .border(4.dp, AudioRecorderBorderThick, RoundedCornerShape(12.dp)) // Thicker, pixelated border
                .shadow(
                    elevation = 6.dp, // Pixelated shadow
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = AudioRecorderShadowGeneral,
                    spotColor = AudioRecorderShadowGeneral
                )
                .padding(horizontal = 24.dp, vertical = 24.dp), // Reduced padding for overall container
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp) // Spacing between sections
        ) {
            Text(
                text = "AUDIO RECORDER",
                fontSize = 18.sp, // Adjusted to 18sp
                fontWeight = FontWeight.Bold,
                color = AudioRecorderTextPrimary,
                fontFamily = PressStart2P,
                modifier = Modifier.padding(bottom = 8.dp) // Spacing below title
            )

            // Control Buttons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Record Button
                val recordInteractionSource = remember { MutableInteractionSource() }
                val isRecordButtonPressed by recordInteractionSource.collectIsPressedAsState()

                Button(
                    onClick = {
                        isRecording = !isRecording
                        if (isRecording) {
                            println("Recording started...")
                            isPaused = false
                            highlightRandomMood()
                        } else {
                            println("Recording stopped.")
                            // Reset to play icon if recording stopped (handled by state below)
                        }
                    },
                    modifier = Modifier
                        .size(70.dp) // Adjusted size for better visual
                        .clip(CircleShape)
                        .background(
                            if (isRecording) AudioRecorderRed else AudioRecorderRecordButton, // Corrected background for non-recording state
                            CircleShape
                        )
                        .border(2.dp, AudioRecorderShadowGeneral, CircleShape)
                        .shadow(
                            elevation = 3.dp,
                            shape = CircleShape,
                            ambientColor = AudioRecorderShadowGeneral,
                            spotColor = AudioRecorderShadowGeneral
                        )
                        .graphicsLayer {
                            // Apply scale/translation for active state
                            val scale = if (isRecordButtonPressed) 0.95f else 1f
                            scaleX = scale
                            scaleY = scale
                            translationX = if (isRecordButtonPressed) 2.dp.toPx() else 0f
                            translationY = if (isRecordButtonPressed) 2.dp.toPx() else 0f
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // Transparent as background is handled by Modifier
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp), // Remove default padding
                    interactionSource = recordInteractionSource
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp) // Smaller inner circle
                            .background(AudioRecorderWhiteCircle, CircleShape)
                    )
                }

                Spacer(Modifier.width(24.dp)) // Space between record and other buttons

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pause/Play Button
                    val pausePlayInteractionSource = remember { MutableInteractionSource() }
                    val isPausePlayButtonPressed by pausePlayInteractionSource.collectIsPressedAsState()
                    Button(
                        onClick = {
                            if (isRecording) {
                                isPaused = !isPaused
                                println(if (isPaused) "Audio paused." else "Audio resumed.")
                            } else {
                                println("No recording to pause/play. Start recording first.")
                                showAlert("No recording to pause/play. Start recording first.", "Info")
                            }
                        },
                        modifier = Modifier
                            .width(100.dp) // Fixed width for consistency
                            .height(48.dp) // Fixed height
                            .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(8.dp),
                                ambientColor = AudioRecorderShadowGeneral,
                                spotColor = AudioRecorderShadowGeneral
                            )
                            .graphicsLayer {
                                val scale = if (isPausePlayButtonPressed) 0.95f else 1f
                                scaleX = scale
                                scaleY = scale
                                translationX = if (isPausePlayButtonPressed) 2.dp.toPx() else 0f
                                translationY = if (isPausePlayButtonPressed) 2.dp.toPx() else 0f
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AudioRecorderButtonDefault,
                            contentColor = Color.White
                        ),
                        interactionSource = pausePlayInteractionSource
                    ) {
                        Text(
                            text = if (isPaused || !isRecording) "\u25B6" else "\u23F8", // Play (&#9654;) or Pause (&#10074;&#10074;)
                            fontSize = 18.sp,
                            fontFamily = PressStart2P,
                            fontWeight = FontWeight.Bold // Added bold for better look
                        )
                    }

                    // Restart Button
                    val restartInteractionSource = remember { MutableInteractionSource() }
                    val isRestartButtonPressed by restartInteractionSource.collectIsPressedAsState()
                    Button(
                        onClick = {
                            println("Recording restarted.")
                            isRecording = false
                            isPaused = false
                            predictedMood = null // Clear highlighted mood
                            showAlert("Recording Restarted", "Info")
                        },
                        modifier = Modifier
                            .width(100.dp) // Fixed width
                            .height(48.dp) // Fixed height
                            .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(8.dp),
                                ambientColor = AudioRecorderShadowGeneral,
                                spotColor = AudioRecorderShadowGeneral
                            )
                            .graphicsLayer {
                                val scale = if (isRestartButtonPressed) 0.95f else 1f
                                scaleX = scale
                                scaleY = scale
                                translationX = if (isRestartButtonPressed) 2.dp.toPx() else 0f
                                translationY = if (isRestartButtonPressed) 2.dp.toPx() else 0f
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AudioRecorderButtonDefault,
                            contentColor = Color.White
                        ),
                        interactionSource = restartInteractionSource
                    ) {
                        Text(
                            text = "\u21BB", // Unicode for restart icon
                            fontSize = 18.sp,
                            fontFamily = PressStart2P,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Predicted Mood Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AudioRecorderMoodSection, RoundedCornerShape(8.dp))
                    .border(2.dp, AudioRecorderBorderThick, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Predicted Mood:",
                    fontSize = 14.sp, // Adjusted size
                    fontWeight = FontWeight.Bold,
                    color = AudioRecorderTextPrimary,
                    fontFamily = PressStart2P,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val moods = listOf(
                    Pair("happy", "😄"),
                    Pair("sad", "😢"),
                    Pair("mad", "😠"),
                    Pair("surprised", "😮"),
                    Pair("neutral", "😐")
                )
                // Use a standard Row for FlowRow if Accompanist not included
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically // Emojis align vertically
                ) {
                    moods.forEach { (moodName, emojiChar) ->
                        Text(
                            text = emojiChar,
                            fontSize = 28.sp, // Larger emoji size
                            // Simulate filter effects by changing color and scale
                            color = if (predictedMood == moodName) AudioRecorderTextPrimary else AudioRecorderTextPrimary.copy(alpha = 0.4f), // Desaturate/dim if not highlighted
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .graphicsLayer {
                                    val scale = if (predictedMood == moodName) 1.1f else 0.9f
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable {
                                    // Optionally allow user to pick mood manually
                                    predictedMood = moodName
                                    println("Manually selected mood: $moodName")
                                }
                        )
                    }
                }
            }

            // Save Button
            val saveInteractionSource = remember { MutableInteractionSource() }
            val isSaveButtonPressed by saveInteractionSource.collectIsPressedAsState()
            Button(
                onClick = {
                    println("Audio saved!")
                    showAlert("Audio Saved!", "Success")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Taller button
                    .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = AudioRecorderShadowGeneral,
                        spotColor = AudioRecorderShadowGeneral
                    )
                    .graphicsLayer {
                        val scale = if (isSaveButtonPressed) 0.95f else 1f
                        scaleX = scale
                        scaleY = scale
                        translationX = if (isSaveButtonPressed) 2.dp.toPx() else 0f
                        translationY = if (isSaveButtonPressed) 2.dp.toPx() else 0f
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AudioRecorderButtonDefault,
                    contentColor = Color.White
                ),
                interactionSource = saveInteractionSource
            ) {
                Text(
                    text = "SAVE",
                    fontSize = 18.sp,
                    fontFamily = PressStart2P,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Custom Alert Message (fixed at bottom)
        AnimatedVisibility(
            visible = alertMessageState != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }), // Slide up from half height
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }), // Slide down to half height
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .widthIn(max = 280.dp) // Constrain max width for alert
        ) {
            alertMessageState?.let { (message, bgColor) ->
                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), // Slight border for depth
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = Color.White, // Keeping Color.White here, can be AudioRecorderWhiteCircle
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily // Use a standard readable font
                    )
                }
            }
        }
    }
}


// Removed the placeholder FlowRow, using a standard Row.
// If you need actual text wrapping for a dynamic number of items,
// consider adding Accompanist FlowLayout or implementing a custom layout.
// For 5 fixed emojis, a Row with Arrangement.Center is usually sufficient.


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AudioRecorderScreenPreview() {
    PixelatedAppTheme {
        AudioRecorderScreen(rememberNavController())
    }
}