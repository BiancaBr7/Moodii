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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.moodii.api.auth.AuthorizedClient
import com.example.moodii.ui.navigation.MoodiiNavGraph
import com.example.moodii.ui.navigation.AppDestinations
import com.example.moodii.ui.theme.PixelatedAppTheme
import com.example.moodii.utils.AuthManager
import com.example.moodii.utils.NetworkDebugUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        
        // Initialize AuthorizedClient with application context
        AuthorizedClient.initialize(applicationContext)
        
        setContent {
            Log.d("MainActivity", "setContent called")
            PixelatedAppTheme {
                Log.d("MainActivity", "PixelatedAppTheme applied")
                
                // Test network connection
                LaunchedEffect(Unit) {
                    NetworkDebugUtil.testMultipleUrls()
                }
                
                // Check authentication status and set start destination
                val authManager = remember { AuthManager(applicationContext) }
                val startDestination = if (authManager.isLoggedIn()) {
                    AppDestinations.DASHBOARD_ROUTE
                } else {
                    AppDestinations.LOGIN_ROUTE
                }
                
                val navController = rememberNavController()
                MoodiiNavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}