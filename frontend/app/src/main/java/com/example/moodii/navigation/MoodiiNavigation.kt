package com.example.moodii.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moodii.ui.auth.login.LoginScreen
import com.example.moodii.ui.auth.register.SignUpScreen
import com.example.moodii.ui.dashboard.DashboardScreen
import com.example.moodii.ui.day.DayViewScreen

@Composable
fun MoodiiNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login" // or "dashboard" if user is already logged in
    ) {
        // Authentication screens
        composable("login") {
            LoginScreen(navController = navController)
        }
        
        composable("register") {
            SignUpScreen(navController = navController)
        }
        
        // Main app screens
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        
        composable("day_view/{selectedDate}") { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            DayViewScreen(
                navController = navController,
                selectedDate = selectedDate
            )
        }
    }
}
