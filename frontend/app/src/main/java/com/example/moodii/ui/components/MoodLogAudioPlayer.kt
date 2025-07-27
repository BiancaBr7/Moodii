package com.example.moodii.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodii.api.moodlog.MoodLogClient
import com.example.moodii.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun MoodLogAudioPlayer(
    modifier: Modifier = Modifier,
    moodLogId: String?,
    onPlayPauseClick: (isPlaying: Boolean) -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(0f) }
    var totalDuration by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0) }
    var progressBarWidthPx by remember { mutableStateOf(0f) }
    var error by remember { mutableStateOf<String?>(null) }
    var audioReady by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    
    // Audio manager for focus handling
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    // Helper to format time
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // Initialize audio when moodLogId changes
    LaunchedEffect(moodLogId) {
        if (moodLogId != null) {
            try {
                isLoading = true
                error = null
                audioReady = false
                Log.d("MoodLogAudioPlayer", "Fetching audio for mood log: $moodLogId")
                
                // Fetch audio file from backend
                val response = MoodLogClient.moodLogService.getAudio(moodLogId)
                
                Log.d("MoodLogAudioPlayer", "Response code: ${response.code()}")
                Log.d("MoodLogAudioPlayer", "Response message: ${response.message()}")
                
                if (response.isSuccessful) {
                    val audioData = response.body()
                    if (audioData != null) {
                        Log.d("MoodLogAudioPlayer", "Audio fetched successfully, size: ${audioData.contentLength()} bytes")
                        
                        // Save audio data to a temporary file  
                        val tempFile = File(context.cacheDir, "temp_audio_${moodLogId}.m4a")
                        try {
                            val inputStream = audioData.byteStream()
                            val outputStream = FileOutputStream(tempFile)
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()
                            
                            audioFile = tempFile
                            Log.d("MoodLogAudioPlayer", "Audio saved to temp file: ${tempFile.absolutePath}")
                            
                            // Initialize MediaPlayer with proper audio attributes
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer().apply {
                                // Set audio attributes for proper audio routing
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                                }
                                
                                setDataSource(tempFile.absolutePath)
                                setVolume(1.0f, 1.0f) // Ensure full volume
                                prepareAsync()
                                setOnPreparedListener { mp ->
                                    totalDuration = mp.duration / 1000 // Convert to seconds
                                    audioReady = true
                                    isLoading = false
                                    Log.d("MoodLogAudioPlayer", "MediaPlayer prepared, duration: ${totalDuration}s")
                                }
                                setOnCompletionListener {
                                    isPlaying = false
                                    currentPosition = 0
                                    currentProgress = 0f
                                    Log.d("MoodLogAudioPlayer", "Audio playback completed")
                                }
                                setOnErrorListener { mp, what, extra ->
                                    Log.e("MoodLogAudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                                    error = "Playback error ($what:$extra)"
                                    isLoading = false
                                    true
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MoodLogAudioPlayer", "Error saving audio to temp file", e)
                            error = "Failed to prepare audio"
                            isLoading = false
                        }
                    } else {
                        Log.e("MoodLogAudioPlayer", "Response body is null")
                        error = "No audio data received"
                        isLoading = false
                    }
                } else {
                    Log.e("MoodLogAudioPlayer", "Failed to fetch audio: ${response.code()} - ${response.message()}")
                    // Try to read error body
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MoodLogAudioPlayer", "Error body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("MoodLogAudioPlayer", "Could not read error body", e)
                    }
                    error = "Audio not available (${response.code()})"
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("MoodLogAudioPlayer", "Error fetching audio", e)
                error = "Failed to load audio: ${e.message}"
                isLoading = false
            }
        }
    }

    // Update progress when playing
    LaunchedEffect(isPlaying) {
        if (isPlaying && totalDuration > 0 && audioReady && mediaPlayer != null) {
            while (isPlaying && currentPosition < totalDuration) {
                delay(100) // Update every 100ms for smoother progress
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        currentPosition = mp.currentPosition / 1000 // Convert to seconds
                        currentProgress = currentPosition.toFloat() / totalDuration.toFloat()
                    }
                }
                
                if (currentPosition >= totalDuration) {
                    isPlaying = false
                    currentPosition = 0
                    currentProgress = 0f
                }
            }
        }
    }

    // Cleanup MediaPlayer
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.release()
                audioFile?.delete() // Clean up temp file
            } catch (e: Exception) {
                Log.e("MoodLogAudioPlayer", "Error during cleanup", e)
            }
        }
    }

    Box(
        modifier = modifier
            .background(AudioRecorderContainerBg, RoundedCornerShape(12.dp))
            .border(2.dp, AudioRecorderBorderThick, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = AudioRecorderButtonDefault,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Loading audio...",
                    fontSize = 10.sp,
                    color = AudioRecorderTextPrimary,
                    fontFamily = PressStart2P
                )
            }
        } else if (error != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = error!!,
                    fontSize = 10.sp,
                    color = AudioRecorderAlertError,
                    fontFamily = PressStart2P
                )
            }
        } else if (audioReady) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress Bar and Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        fontSize = 8.sp,
                        color = AudioRecorderTextPrimary,
                        fontFamily = PressStart2P,
                        modifier = Modifier.widthIn(min = 35.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(AudioRecorderBackgroundPage)
                            .border(1.dp, AudioRecorderBorderThick, RoundedCornerShape(3.dp))
                            .onGloballyPositioned { coordinates ->
                                progressBarWidthPx = coordinates.size.width.toFloat()
                            }
                            .pointerInput(progressBarWidthPx) {
                                detectDragGestures { change, _ ->
                                    if (audioReady && mediaPlayer != null) {
                                        val newProgress = (change.position.x / progressBarWidthPx).coerceIn(0f, 1f)
                                        currentProgress = newProgress
                                        currentPosition = (newProgress * totalDuration).roundToInt()
                                        
                                        // Seek to new position
                                        val seekPosition = (newProgress * totalDuration * 1000).toInt() // Convert to milliseconds
                                        mediaPlayer?.seekTo(seekPosition)
                                        Log.d("MoodLogAudioPlayer", "Seeked to position: ${currentPosition}s")
                                    }
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(currentProgress)
                                .background(AudioRecorderButtonDefault, RoundedCornerShape(3.dp))
                        )
                    }

                    Text(
                        text = formatTime(totalDuration),
                        fontSize = 8.sp,
                        color = AudioRecorderTextPrimary,
                        fontFamily = PressStart2P,
                        modifier = Modifier.widthIn(min = 35.dp)
                    )
                }

                // Control Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = AudioRecorderTextPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { 
                                mediaPlayer?.seekTo(0)
                                currentPosition = 0
                                currentProgress = 0f
                                onPreviousClick() 
                            }
                    )

                    // Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AudioRecorderButtonDefault)
                            .clickable {
                                if (audioReady && error == null && mediaPlayer != null) {
                                    try {
                                        if (isPlaying) {
                                            mediaPlayer?.pause()
                                            isPlaying = false
                                            Log.d("MoodLogAudioPlayer", "Audio paused")
                                        } else {
                                            // Request audio focus before playing
                                            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                                    .setAudioAttributes(
                                                        AudioAttributes.Builder()
                                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                                            .build()
                                                    )
                                                    .build()
                                                audioManager.requestAudioFocus(focusRequest)
                                            } else {
                                                @Suppress("DEPRECATION")
                                                audioManager.requestAudioFocus(
                                                    null,
                                                    AudioManager.STREAM_MUSIC,
                                                    AudioManager.AUDIOFOCUS_GAIN
                                                )
                                            }
                                            
                                            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                                mediaPlayer?.start()
                                                isPlaying = true
                                                Log.d("MoodLogAudioPlayer", "Audio started")
                                            } else {
                                                Log.w("MoodLogAudioPlayer", "Could not gain audio focus")
                                                error = "Could not gain audio focus"
                                            }
                                        }
                                        onPlayPauseClick(isPlaying)
                                    } catch (e: Exception) {
                                        Log.e("MoodLogAudioPlayer", "Error controlling playback", e)
                                        error = "Playback control error: ${e.message}"
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Next Button
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = AudioRecorderTextPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { 
                                mediaPlayer?.let { mp ->
                                    mp.seekTo(mp.duration)
                                    isPlaying = false
                                    currentPosition = totalDuration
                                    currentProgress = 1f
                                }
                                onNextClick() 
                            }
                    )
                }
            }
        } else {
            // No audio available
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No audio available",
                    fontSize = 10.sp,
                    color = AudioRecorderTextPrimary.copy(alpha = 0.6f),
                    fontFamily = PressStart2P
                )
            }
        }
    }
}
