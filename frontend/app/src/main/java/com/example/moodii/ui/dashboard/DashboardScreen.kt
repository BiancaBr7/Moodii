package com.example.moodii.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.moodii.ui.screens.AudioRecorderScreen
import com.example.moodii.ui.navigation.AppDestinations
import com.example.moodii.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddMoodDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AudioRecorderBackgroundPage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with logout button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MOOD TRACKER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AudioRecorderTextPrimary,
                    fontFamily = PressStart2P,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                IconButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(AppDestinations.LOGIN_ROUTE) {
                            popUpTo(AppDestinations.DASHBOARD_ROUTE) { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = AudioRecorderTextPrimary
                    )
                }
            }

            // Calendar
            TrackerCalendar(
                currentMonth = state.currentMonth,
                moodLogs = state.moodLogsForMonth,
                onDateClick = { date ->
                    viewModel.selectDate(date)
                    navController.navigate("${AppDestinations.DAY_VIEW_ROUTE}/${date}")
                },
                onPreviousMonth = { viewModel.navigateToPreviousMonth() },
                onNextMonth = { viewModel.navigateToNextMonth() },
                isLoading = state.isLoading
            )

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
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Floating Add Button
        FloatingActionButton(
            onClick = { showAddMoodDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AudioRecorderButtonDefault,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Mood",
                modifier = Modifier.size(24.dp)
            )
        }

        // Add Mood Dialog
        if (showAddMoodDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showAddMoodDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { /* Prevent dialog from closing when clicking inside */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AudioRecorderContainerBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ADD MOOD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AudioRecorderTextPrimary,
                                fontFamily = PressStart2P
                            )
                            
                            Button(
                                onClick = { showAddMoodDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AudioRecorderAlertError
                                ),
                                modifier = Modifier.size(32.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("×", color = Color.White, fontSize = 18.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Audio Recorder Component
                        AudioRecorderScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackerCalendar(
    currentMonth: String,
    moodLogs: List<MoodLog>,
    onDateClick: (String) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    isLoading: Boolean
) {
    // Parse current month string to get calendar info
    val calendar = Calendar.getInstance()
    val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    try {
        calendar.time = monthFormat.parse(currentMonth) ?: Date()
    } catch (e: Exception) {
        calendar.time = Date() // Fallback to current date
    }
    
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    
    // Get first day of month and calculate starting position
    calendar.set(year, month, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Create mood log map for quick lookup
    val moodLogsByDate = moodLogs.groupBy { 
        it.createdAt?.take(10) ?: "1970-01-01"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AudioRecorderContainerBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = AudioRecorderTextPrimary
                    )
                }
                
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AudioRecorderTextPrimary,
                    fontFamily = PressStart2P
                )
                
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = AudioRecorderTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AudioRecorderTextPrimary,
                        fontFamily = PressStart2P
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Empty cells for days before month starts
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.size(40.dp))
                }

                // Days of the month
                items(daysInMonth) { day ->
                    val dayNumber = day + 1
                    val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayNumber)
                    val moodLogsForDay = moodLogsByDate[dateString] ?: emptyList()
                    val hasMoodLogs = moodLogsForDay.isNotEmpty()
                    val dominantMood = if (hasMoodLogs) {
                        // Get the most recent mood for the day
                        moodLogsForDay.maxByOrNull { it.createdAt ?: "" }?.moodType
                    } else null

                    CalendarDay(
                        day = dayNumber,
                        hasMoodLogs = hasMoodLogs,
                        moodType = dominantMood,
                        onClick = { onDateClick(dateString) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AudioRecorderButtonDefault,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    hasMoodLogs: Boolean,
    moodType: Int?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !hasMoodLogs -> Color.Transparent
        else -> getMoodColor(moodType ?: 5)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (hasMoodLogs) 2.dp else 1.dp,
                color = if (hasMoodLogs) AudioRecorderTextPrimary else AudioRecorderTextPrimary.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 12.sp,
            fontWeight = if (hasMoodLogs) FontWeight.Bold else FontWeight.Normal,
            color = if (hasMoodLogs) Color.White else AudioRecorderTextPrimary,
            fontFamily = PressStart2P
        )
    }
}

private fun getMoodColor(moodType: Int): Color {
    return when (moodType) {
        1 -> Color(0xFF4CAF50) // Happy - Green
        2 -> Color(0xFF2196F3) // Sad - Blue
        3 -> Color(0xFFF44336) // Mad - Red
        4 -> Color(0xFFFF9800) // Surprised - Orange
        5 -> Color(0xFF9E9E9E) // Neutral - Gray
        else -> Color(0xFF9E9E9E)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    PixelatedAppTheme {
        DashboardScreen(rememberNavController())
    }
}
