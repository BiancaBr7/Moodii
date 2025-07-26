package com.example.moodii.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moodii.ui.screens.AudioRecorderScreen
import com.example.moodii.ui.screens.LoginScreen
import com.example.moodii.ui.screens.SignUpScreen
import com.example.moodii.ui.screens.LogListScreen
import com.example.moodii.ui.dashboard.DashboardScreen
import com.example.moodii.ui.day.DayViewScreen

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val DASHBOARD_ROUTE = "dashboard"
    const val AUDIO_ROUTE = "record"
    const val LOG_ENTRIES_ROUTE = "log_entries"
    const val DAY_VIEW_ROUTE = "day_view"
}

@Composable
fun MoodiiNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppDestinations.LOGIN_ROUTE // Or HOME_ROUTE if login state is handled
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                navController = navController
                // Navigation is handled inside LoginViewModel/LoginScreen
                // On successful login, navigate to dashboard:
                // navController.navigate(AppDestinations.DASHBOARD_ROUTE) {
                //     popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                //     launchSingleTop = true
                // }
            )
        }

        composable(AppDestinations.SIGNUP_ROUTE) {
            SignUpScreen(
                navController = navController
                // Navigation is handled inside RegisterViewModel/SignUpScreen
                // On successful signup, navigate to dashboard or back to login
            )
        }

        // Main dashboard with calendar
        composable(AppDestinations.DASHBOARD_ROUTE) {
            DashboardScreen(navController = navController)
        }

        // Day view for specific date
        composable("${AppDestinations.DAY_VIEW_ROUTE}/{selectedDate}") { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            DayViewScreen(
                navController = navController,
                selectedDate = selectedDate
            )
        }

        // Audio recorder (now integrated into dashboard as dialog)
        composable(AppDestinations.AUDIO_ROUTE) {
            AudioRecorderScreen(navController = navController)
        }

        // Log entries list (can be replaced by day view)
        composable(AppDestinations.LOG_ENTRIES_ROUTE) {
            LogListScreen(
                navController = navController
            )
        }

        // Add more destinations here using composable()
        // For example, if TrashCan led to a specific screen:
        // composable("deleted_items") { DeletedItemsScreen(navController) }
    }
}
