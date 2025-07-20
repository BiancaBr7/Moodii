package com.example.moodii.ui.navigation // Or your preferred package

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moodii.ui.screens.AudioRecorderScreen
import com.example.moodii.ui.screens.LoginScreen
import com.example.moodii.ui.screens.SignUpScreen
import com.example.moodii.ui.screens.LogListScreen

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val AUDIO_ROUTE = "record"
    const val LOG_ENTRIES_ROUTE = "log_entries"
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
                navController = navController,
//                onLoginSuccess = {
//                    navController.navigate(AppDestinations.HOME_ROUTE) {
//                        // Pop up to login screen to remove it from back stack
//                        popUpTo(AppDestinations.LOGIN_ROUTE) {
//                            inclusive = true
//                        }
//                        // Avoid multiple copies of the home screen
//                        launchSingleTop = true
//                    }
//                },
//                onNavigateToSignup = {
//                    navController.navigate(AppDestinations.SIGNUP_ROUTE)
//                }
            )
        }

        composable(AppDestinations.SIGNUP_ROUTE) {
            SignUpScreen(
                navController = navController,
//                onSignupSuccess = {
//                    navController.navigate(AppDestinations.HOME_ROUTE) {
//                        popUpTo(AppDestinations.SIGNUP_ROUTE) { // Or popUpTo(LOGIN_ROUTE) if signup is on top of login
//                            inclusive = true
//                        }
//                        launchSingleTop = true
//                    }
//                },
//                onNavigateBackToLogin = {
//                    navController.popBackStack()
//                }
            )
        }

        composable(AppDestinations.AUDIO_ROUTE) {
            LogListScreen(
                navController = navController,
//                onNavigateToLogEntries = {
//                    navController.navigate(AppDestinations.LOG_ENTRIES_ROUTE)
//                }
//                // Add other navigation actions from home screen
            )
        }

        composable(AppDestinations.LOG_ENTRIES_ROUTE) {
            AudioRecorderScreen(
                navController = navController
                // You'll likely pass a ViewModel or data source here
            )
        }

        // Add more destinations here using composable()
        // For example, if TrashCan led to a specific screen:
        // composable("deleted_items") { DeletedItemsScreen(navController) }
    }
}
