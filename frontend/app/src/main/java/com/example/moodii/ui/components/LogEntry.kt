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
import com.example.moodii.data.moodlog.MoodLog

// Define the .em() extension function here or in a common utility file
@Composable
fun Float.em(): TextUnit {
    val density = LocalDensity.current
    return (this * density.fontScale).sp
}

@Composable
fun LogEntry(
    modifier: Modifier = Modifier,
    moodLog: MoodLog,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
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
                            modifier = Modifier.padding(end = 8.dp)
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
                            text = formatDateFromCreatedAt(moodLog.createdAt),
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
                            .padding(16.dp) // Increased padding for better spacing
                            .clickable(onClick = onEditClick),
                        contentAlignment = Alignment.CenterStart // Left-align the content
                    ) {
                        Text(
                            text = buildMoodContent(moodLog),
                            color = PixelatedPurpleDarker,
                            fontSize = 12.sp,
                            fontFamily = PressStart2P,
                            lineHeight = 18.sp, // Fixed line height for better spacing
                            softWrap = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } // End of Webpage section

                // Bottom section: Audio Player and Trash Can
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp), // Increased padding for better spacing
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Audio Player (takes up available space) - only show if audio exists
                    // For now, we'll always show the audio player since we don't have an audioFilePath property
                    // In a real implementation, you would check if audio exists for this mood log
                    MoodLogAudioPlayer(
                        moodLogId = moodLog.id,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Space between player and garbage can

                    // Garbage Can Icon (fixed size, aligned to the right)
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TrashCan(
                            onClick = {
                                println("LogEntry: TrashCan clicked for mood log: ${moodLog.id}")
                                onDeleteClick()
                            },
                            modifier = Modifier.size(48.dp) // Slightly smaller for better proportions
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
        LogEntry(
            moodLog = com.example.moodii.data.moodlog.MoodLog(
                id = "1",
                title = "My first mood log",
                transcription = "I'm feeling really good today!",
                moodType = 1, // Happy
                userId = "1",
                createdAt = "2025-07-26T14:30:00Z"
            )
        )
    }
}

// Helper function to format date from createdAt timestamp
private fun formatDateFromCreatedAt(createdAt: String?): String {
    return if (createdAt?.isNotBlank() == true) {
        try {
            // Extract date part from ISO timestamp (e.g., "2025-01-27T14:30:00Z" -> "Jan 27, 2025")
            val datePart = createdAt.split("T")[0] // Get "2025-01-27"
            val parts = datePart.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = when(parts[1]) {
                    "01" -> "Jan"
                    "02" -> "Feb" 
                    "03" -> "Mar"
                    "04" -> "Apr"
                    "05" -> "May"
                    "06" -> "Jun"
                    "07" -> "Jul"
                    "08" -> "Aug"
                    "09" -> "Sep"
                    "10" -> "Oct"
                    "11" -> "Nov"
                    "12" -> "Dec"
                    else -> parts[1]
                }
                val day = parts[2].toIntOrNull()?.toString() ?: parts[2]
                "$month $day, $year"
            } else {
                createdAt
            }
        } catch (e: Exception) {
            createdAt
        }
    } else {
        "Unknown date"
    }
}

// Helper function to build mood content display
private fun buildMoodContent(moodLog: MoodLog): String {
    val moodName = getMoodName(moodLog.moodType)
    val moodEmoji = getMoodEmoji(moodLog.moodType)
    
    return buildString {
        if (moodLog.title.isNotBlank()) {
            append("📝 ${moodLog.title}")
            append("\n\n")
        }
        append("${moodEmoji} Mood: ${moodName}")
        if (moodLog.transcription.isNotBlank()) {
            append("\n\n")
            append("🎤 \"${moodLog.transcription}\"")
        }
    }
}

private fun getMoodEmoji(moodType: Int): String {
    return when (moodType) {
        1 -> "😄" // Happy
        2 -> "😢" // Sad
        3 -> "😠" // Mad
        4 -> "😮" // Surprised
        5 -> "😐" // Neutral
        else -> "😐"
    }
}

private fun getMoodName(moodType: Int): String {
    return when (moodType) {
        1 -> "Happy"
        2 -> "Sad"
        3 -> "Mad"
        4 -> "Surprised"
        5 -> "Neutral"
        else -> "Unknown"
    }
}