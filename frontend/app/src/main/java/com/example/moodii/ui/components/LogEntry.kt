package com.example.moodii.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape // Added for overall box shape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodii.ui.components.TrashCan // Ensure this import is correct
import com.example.moodii.ui.theme.PixelatedAppTheme
import com.example.moodii.ui.theme.PixelatedPurpleDarker
import com.example.moodii.ui.theme.PixelatedPurpleMedium
import com.example.moodii.ui.theme.PixelatedWhite
import com.example.moodii.ui.theme.PressStart2P
import androidx.compose.ui.platform.LocalDensity // For .em() extension
import androidx.compose.ui.unit.TextUnit // For .em() extension
import com.example.moodii.ui.components.AudioPlayer

// Define the .em() extension function here or in a common utility file
@Composable
fun Float.em(): TextUnit {
    val density = LocalDensity.current
    return (this * density.fontScale).sp
}

@Composable
fun LogEntry(
    modifier: Modifier = Modifier,
    logContent: String = "EMPTY BLOG POST\nCLICK TO EDIT",
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    initialAudioProgress: Float = 0f,
    totalAudioDurationSeconds: Int = (2 * 60) + 36
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Spacing between log entries
    ) {
        // Main container box for the entire log entry (webpage + player + trashcan)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp), // Overall shadow with rounded corners
                    ambientColor = PixelatedPurpleDarker,
                    spotColor = PixelatedPurpleDarker
                )
                .border(2.dp, PixelatedPurpleDarker, RoundedCornerShape(12.dp)) // Overall border
                .background(PixelatedPurpleMedium, RoundedCornerShape(12.dp)) // Overall background
        ) {
            Column( // This Column will arrange the webpage section and the player/trashcan section vertically
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top section: Fake Webpage Header & Content
                Column( // Encapsulates webpage elements
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Top bar of the fake webpage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color(0xFFC2A7E0)) // bg-[#c2a7e0]
                            .border(2.dp, PixelatedPurpleDarker, RectangleShape), // pixel-border-bottom
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.padding(start = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(PixelatedPurpleMedium, CircleShape)
                            )
                            Spacer(Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(PixelatedPurpleMedium, CircleShape)
                            )
                        }
                        Text(
                            text = "X",
                            color = PixelatedPurpleDarker, // text-[#5a3d7a]
                            fontSize = 12.sp,
                            fontFamily = PressStart2P,
                            modifier = Modifier.padding(end = 8.dp).clickable(onClick = onDeleteClick)
                        )
                    }

                    // Address bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(PixelatedWhite)
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ">",
                            color = PixelatedPurpleDarker,
                            fontSize = 8.sp,
                            fontFamily = PressStart2P
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "July 27, 2025",
                            color = PixelatedPurpleDarker,
                            fontSize = 8.sp,
                            fontFamily = PressStart2P,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Main content area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PixelatedPurpleMedium) // Background for content area, or use PixelatedPurpleMedium if that's the goal
                            .padding(top = 16.dp, bottom = 32.dp, start = 10.dp, end = 10.dp)
                            .clickable(onClick = onEditClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = logContent,
                            color = PixelatedPurpleDarker,
                            fontSize = 12.sp,
                            fontFamily = PressStart2P,
                            lineHeight = 1.2f.em(), // Adjust line height for pixelated text
                            softWrap = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } // End of Webpage section

                // Bottom section: Audio Player and Trash Can
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp), // Padding for this entire row within the main box
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // Distributes content
                ) {
                    // Audio Player (takes up available space)
                    AudioPlayer(
                        modifier = Modifier.weight(1f), // Make it fill available width
                        initialProgress = initialAudioProgress,
                        totalDurationSeconds = totalAudioDurationSeconds
                    )

                    Spacer(Modifier.width(8.dp)) // Space between player and garbage can

                    // Garbage Can Icon (fixed size, aligned to the right by SpaceBetween)
                    Box(
                        modifier = Modifier.width(60.dp), // Define the width for this section
                        contentAlignment = Alignment.Center // Centers content (TrashCan) horizontally AND vertically within this Box
                    ) {
                        TrashCan(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(50.dp) // TrashCan itself maintains its size
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PixelatedLogEntryPreview() {
    PixelatedAppTheme {
        LogEntry(logContent = "My amazing log entry!")
    }
}