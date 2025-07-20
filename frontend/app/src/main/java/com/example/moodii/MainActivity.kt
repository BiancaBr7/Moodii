package com.example.moodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.navigation.NavGraph
import com.example.moodii.ui.theme.PixelatedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixelatedAppTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}