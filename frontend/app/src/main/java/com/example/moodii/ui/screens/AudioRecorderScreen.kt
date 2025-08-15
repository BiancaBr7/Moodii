package com.example.moodii.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.components.AudioPlayer
import com.example.moodii.ui.audio.AudioRecorderViewModel
import com.example.moodii.ui.components.AppBackground
import com.example.moodii.ui.components.AppLogo
import com.example.moodii.ui.components.LogoSize
import com.example.moodii.ui.theme.* // Import all custom colors and theme components

// Extension function for pixelated font sizing (from previous discussions)
@Composable
fun Float.em(): androidx.compose.ui.unit.TextUnit {
    val density = LocalDensity.current
    return (this * density.fontScale).sp
}

@Composable
fun AudioRecorderScreen(
    navController: NavHostController,
    viewModel: AudioRecorderViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    // Check permissions on first launch
    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    // Handle permission request
    LaunchedEffect(state.needsPermission) {
        if (state.needsPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle alert messages
    LaunchedEffect(state.alertMessage) {
        state.alertMessage?.let {
            kotlinx.coroutines.delay(3000) // 3 seconds
            viewModel.clearAlert()
        }
    }

    // --- Helper Functions ---
    val showAlert = { message: String, type: String ->
        val bgColor = when (type) {
            "Success" -> AudioRecorderAlertSuccess
            "Error" -> AudioRecorderAlertError
            else -> AudioRecorderAlertInfo
        }
        // Note: Alert is now handled by ViewModel
    }

    AppBackground(
        showOverlay = true,
        overlayAlpha = 0.6f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
//                .shadow(
//                    elevation = 6.dp, // Pixelated shadow
//                    shape = RoundedCornerShape(12.dp),
//                    ambientColor = AudioRecorderShadowGeneral,
//                    spotColor = AudioRecorderShadowGeneral
//                )
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
                        if (state.isRecording) {
                            viewModel.stopRecording()
                        } else {
                            viewModel.startRecording()
                        }
                    },
                    modifier = Modifier
                        .size(70.dp) // Adjusted size for better visual
                        .clip(CircleShape)
                        .background(
                            if (state.isRecording) AudioRecorderRed else AudioRecorderRecordButton, // Corrected background for non-recording state
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
                            if (state.isRecording) {
                                if (state.isPaused) {
                                    viewModel.resumeRecording()
                                } else {
                                    viewModel.pauseRecording()
                                }
                            } else {
                                // Show alert if no recording to pause/play
                                println("No recording to pause/play. Start recording first.")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(100.dp) // Fixed width for consistency
                            .height(48.dp) // Fixed height
//                            .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
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
                            text = if (state.isPaused || !state.isRecording) "\u25B6" else "\u23F8", // Play (&#9654;) or Pause (&#10074;&#10074;)
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
                            viewModel.restartRecording()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(100.dp) // Fixed width
                            .height(48.dp) // Fixed height
//                            .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
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
//                    .border(2.dp, AudioRecorderBorderThick, RoundedCornerShape(8.dp))
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
                            color = if (state.predictedMood == moodName) AudioRecorderTextPrimary else AudioRecorderTextPrimary.copy(alpha = 0.4f), // Desaturate/dim if not highlighted
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .graphicsLayer {
                                    val scale = if (state.predictedMood == moodName) 1.1f else 0.9f
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable {
                                    // Allow user to pick mood manually
                                    viewModel.selectMood(moodName)
                                    println("Manually selected mood: $moodName")
                                }
                        )
                    }
                }
            }

            // Transcription Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AudioRecorderMoodSection, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Transcription:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AudioRecorderTextPrimary,
                        fontFamily = PressStart2P
                    )
                    
                    if (state.isTranscribing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎤",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .graphicsLayer {
                                    // Simple pulsing animation for recording indicator
                                    val scale = 1f + 0.1f * kotlin.math.sin(System.currentTimeMillis() * 0.01f)
                                    scaleX = scale
                                    scaleY = scale
                                }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(AudioRecorderBackgroundPage, RoundedCornerShape(4.dp))
                        .border(1.dp, AudioRecorderTextPrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    contentAlignment = if (state.transcription.isBlank()) Alignment.Center else Alignment.TopStart
                ) {
                    if (state.transcription.isBlank()) {
                        Text(
                            text = if (state.isTranscribing) "Listening..." 
                                  else if (state.transcriptionError != null) "Transcription error: ${state.transcriptionError}"
                                  else "Start recording to see live transcription",
                            fontSize = 10.sp,
                            color = AudioRecorderTextPrimary.copy(alpha = 0.6f),
                            fontFamily = PressStart2P,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val scrollState = rememberScrollState()
                        Text(
                            text = state.transcription,
                            fontSize = 10.sp,
                            color = AudioRecorderTextPrimary,
                            fontFamily = PressStart2P,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                        )
                    }
                }
            }

            // Save Button
            val saveInteractionSource = remember { MutableInteractionSource() }
            val isSaveButtonPressed by saveInteractionSource.collectIsPressedAsState()
            Button(
                onClick = {
                    viewModel.saveMoodLog()
                },
                enabled = !state.isSaving,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Taller button
//                    .border(2.dp, AudioRecorderShadowGeneral, RoundedCornerShape(8.dp))
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
                    text = if (state.isSaving) "SAVING..." else "SAVE",
                    fontSize = 18.sp,
                    fontFamily = PressStart2P,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Test Save Button (for development)
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(
//                onClick = {
//                    viewModel.testSaveMoodLog()
//                },
//                enabled = !state.isSaving,
//                shape = RoundedCornerShape(8.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF32CD32), // LimeGreen for test button
//                    contentColor = Color.White
//                )
//            ) {
//                Text(
//                    text = "TEST SAVE (No Recording)",
//                    fontSize = 14.sp,
//                    fontFamily = PressStart2P,
//                    fontWeight = FontWeight.Bold
//                )
//            }
        }

        // Custom Alert Message (fixed at bottom)
        AnimatedVisibility(
            visible = state.alertMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }), // Slide up from half height
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }), // Slide down to half height
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .widthIn(max = 280.dp) // Constrain max width for alert
        ) {
            state.alertMessage?.let { (message, type) ->
                val bgColor = when (type) {
                    "Success" -> AudioRecorderAlertSuccess
                    "Error" -> AudioRecorderAlertError
                    else -> AudioRecorderAlertInfo
                }
                
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