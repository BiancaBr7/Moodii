package com.example.moodii

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.navigation.MoodiiNavGraph
import com.example.moodii.ui.theme.PixelatedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        
        setContent {
            Log.d("MainActivity", "setContent called")
            PixelatedAppTheme {
                Log.d("MainActivity", "PixelatedAppTheme applied")
                
                // Temporary debug screen - uncomment this to test if Compose is working
                // TestScreen()
                
                // Production navigation
                val navController = rememberNavController()
                MoodiiNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun TestScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TEST SCREEN - If you see this, Compose is working!",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}