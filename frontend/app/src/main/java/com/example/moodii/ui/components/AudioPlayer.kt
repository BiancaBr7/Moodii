package com.example.moodii.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// Corrected imports for Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious // Corrected import
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.moodii.ui.theme.PixelatedAppTheme
import com.example.moodii.ui.theme.PixelatedPurpleAccent
import com.example.moodii.ui.theme.PixelatedPurpleDark
import com.example.moodii.ui.theme.PixelatedPurpleMedium
import com.example.moodii.ui.theme.PixelatedPurpleText
import com.example.moodii.ui.theme.PixelatedPurpleTrack
import com.example.moodii.ui.theme.PressStart2P
import kotlin.math.roundToInt


@Preview
@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    initialProgress: Float = 0f, // 0.0 to 1.0
    totalDurationSeconds: Int = (2 * 60) + 36,
    onPlayPauseClick: (isPlaying: Boolean) -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSeek: (progress: Float) -> Unit = {}
) {
    var currentProgress by remember { mutableStateOf(initialProgress) }
    var isPlaying by remember { mutableStateOf(false) }
    var progressBarWidthPx by remember { mutableStateOf(0f) } // Renamed to avoid confusion with Dp
    val density = LocalDensity.current //

    // Helper to format time
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    Box(
        modifier = modifier
            .background(PixelatedPurpleMedium, RoundedCornerShape(12.dp))
            .padding(10.dp) // Reduced padding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced gap
            modifier = Modifier.fillMaxWidth()
        ) {
            // Progress Bar and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Reduced gap
            ) {
                Text(
                    text = formatTime((currentProgress * totalDurationSeconds).roundToInt()),
                    fontSize = 0.7f.em(), // Smaller font size
                    color = PixelatedPurpleText,
                    fontFamily = PressStart2P,
                    modifier = Modifier.widthIn(min = 40.dp) // Ensure fixed width for time
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp) // Even smaller pixelated height
                        .clip(RoundedCornerShape(3.dp))
                        .background(PixelatedPurpleTrack)
                        .border(1.dp, PixelatedPurpleDark, RoundedCornerShape(3.dp)) // Thin border
                        .onGloballyPositioned { coordinates ->
                            progressBarWidthPx = coordinates.size.width.toFloat() // Store width in pixels
                        }
                        .pointerInput(progressBarWidthPx) { // Use progressBarWidthPx as key for recomposition
                            detectDragGestures(
                                onDragStart = { offset ->
                                    // Calculate progress based on the initial touch point on the bar
                                    val newProgress = (offset.x / progressBarWidthPx).coerceIn(0f, 1f)
                                    currentProgress = newProgress
                                    onSeek(newProgress)
                                },
                                onDragEnd = {},
                                onDragCancel = {},
                                onDrag = { change, _ ->
                                    // Calculate progress based on cumulative drag within the bar
                                    val newProgress = (change.position.x / progressBarWidthPx)
                                        .coerceIn(0f, 1f)
                                    currentProgress = newProgress
                                    onSeek(newProgress)
                                }
                            )
                        }
                        // For a simple click to seek (without drag), use this:
                        .clickable {
                            // This clickable gets the overall Box dimensions.
                            // To get the click offset, you'd combine it with pointerInput
                            // or use a BoxWithConstraints. For simplicity, if drag covers seek,
                            // this standalone clickable might not be strictly needed for seeking by direct touch,
                            // but can be for general interaction. Let's make it work by getting offset from click.
                            // To get exact offset for a simple click, you'd still need pointerInput or similar.
                            // If it's just for general click, remove the 'offset' parameter.
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(currentProgress)
                            .background(PixelatedPurpleDark, RoundedCornerShape(3.dp))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp) // Even smaller pixelated thumb size
                            .clip(CircleShape)
                            .background(PixelatedPurpleText)
                            .border(2.dp, Color(0xFF5B0E8C), CircleShape) // Distinct border
                            .shadow(1.dp, CircleShape, ambientColor = Color(0xFF3D075E)) // Mini shadow
                            .align(Alignment.CenterStart)
                            // Corrected offset calculation to use Dp units
                            .offset(x = with(density) { (currentProgress * progressBarWidthPx).toDp() } - 5.dp) // Center thumb manually
                    )
                }

                Text(
                    text = formatTime(totalDurationSeconds),
                    fontSize = 0.7f.em(),
                    color = PixelatedPurpleText,
                    fontFamily = PressStart2P,
                    modifier = Modifier.widthIn(min = 40.dp)
                )
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                ControlButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(40.dp) // Slightly smaller buttons
                ) {
                    Icon(
                        Icons.Filled.SkipPrevious, // Corrected reference
                        contentDescription = "Previous",
                        tint = PixelatedPurpleText,
                        modifier = Modifier.size(24.dp) // Slightly smaller icon
                    )
                }

                Spacer(Modifier.width(20.dp)) // Gap

                // Play/Pause Button
                ControlButton(
                    onClick = {
                        isPlaying = !isPlaying
                        onPlayPauseClick(isPlaying)
                    },
                    modifier = Modifier
                        .size(55.dp) // Slightly smaller, more squarish play button
                        .clip(RoundedCornerShape(40)) // Less circular, more 'squishy' pixel circle
                        .shadow(
                            elevation = 6.dp, // Even chunkier shadow
                            shape = RoundedCornerShape(40),
                            ambientColor = PixelatedPurpleAccent,
                            spotColor = PixelatedPurpleAccent
                        )
                        .background(PixelatedPurpleTrack)
                        .border(3.dp, PixelatedPurpleDark, RoundedCornerShape(40)) // Border for play button
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, // Corrected references
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = PixelatedPurpleText,
                        modifier = Modifier.size(34.dp) // Slightly smaller icon
                    )
                }

                Spacer(Modifier.width(20.dp)) // Gap

                // Next Button
                ControlButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipNext, // Corrected reference
                        contentDescription = "Next",
                        tint = PixelatedPurpleText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp, // Chunkier shadow
                shape = RoundedCornerShape(10.dp),
                ambientColor = PixelatedPurpleAccent,
                spotColor = PixelatedPurpleAccent
            )
            .border(3.dp, PixelatedPurpleDark, RoundedCornerShape(10.dp)) // Thicker border
            .background(PixelatedPurpleTrack, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(4.dp), // Adjust padding if needed
        contentAlignment = Alignment.Center,
        content = content
    )
}

// Extension function for `sp` to `em` like behavior
//@Composable // Mark as Composable because it uses LocalDensity.current
//fun Float.em() = (this * LocalDensity.current.fontScale).sp

// Helper function to convert pixels to Dp
// This function needs to be marked @Composable or take Density as a parameter
@Composable // Mark as Composable because it uses LocalDensity.current
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}


@Preview(showBackground = true)
@Composable
fun AudioPlayerPreview() {
    PixelatedAppTheme {
        AudioPlayer()
    }
}