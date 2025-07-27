package com.example.moodii.ui.day

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.data.moodlog.MoodLog
import com.example.moodii.ui.components.LogEntry
import com.example.moodii.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DayViewScreen(
    navController: NavHostController,
    selectedDate: String,
    viewModel: DayViewViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.loadMoodLogsForDate(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AudioRecorderBackgroundPage)
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AudioRecorderTextPrimary
                )
            }
            
            Text(
                text = formatDateDisplay(selectedDate),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AudioRecorderTextPrimary,
                fontFamily = PressStart2P,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Spacer to balance the back button
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AudioRecorderButtonDefault)
            }
        } else if (state.moodLogsForDay.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AudioRecorderContainerBg)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No mood logs for this day",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AudioRecorderTextPrimary,
                            fontFamily = PressStart2P,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AudioRecorderButtonDefault
                            )
                        ) {
                            Text(
                                text = "GO BACK",
                                fontFamily = PressStart2P,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Mood logs list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.moodLogsForDay) { moodLog ->
                    LogEntry(
                        moodLog = moodLog,
                        onEditClick = {
                            // TODO: Implement edit functionality
                            println("Edit mood log: ${moodLog.id}")
                        },
                        onDeleteClick = {
                            println("DayViewScreen: Delete clicked for mood log: ${moodLog.id}")
                            moodLog.id?.let { id ->
                                println("DayViewScreen: Calling viewModel.deleteMoodLog with ID: $id")
                                viewModel.deleteMoodLog(id)
                            } ?: println("DayViewScreen: Mood log ID is null!")
                        }
                    )
                }
            }
        }

        // Error display
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = AudioRecorderAlertError)
            ) {
                Text(
                    text = error,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = PressStart2P,
                    fontSize = 10.sp
                )
            }
        }
    }
}

private fun buildLogContent(moodLog: MoodLog): String {
    val time = formatTime(moodLog.createdAt ?: "")
    val moodEmoji = getMoodEmoji(moodLog.moodType)
    val moodName = getMoodName(moodLog.moodType)
    
    return """
        📅 ${time}
        ${moodEmoji} Mood: ${moodName}
        
        📝 Transcription:
        ${moodLog.transcription}
    """.trimIndent()
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
        else -> "Neutral"
    }
}

private fun formatTime(dateTimeString: String): String {
    return try {
        // Assuming the format is ISO datetime, extract time part
        val time = dateTimeString.substring(11, 16) // HH:MM
        time
    } catch (e: Exception) {
        "00:00"
    }
}

@Preview(showBackground = true)
@Composable
fun DayViewScreenPreview() {
    PixelatedAppTheme {
        DayViewScreen(
            navController = rememberNavController(),
            selectedDate = "2025-01-26"
        )
    }
}

// Helper function to format date string for display
private fun formatDateDisplay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString // Fallback to original string if parsing fails
    }
}
