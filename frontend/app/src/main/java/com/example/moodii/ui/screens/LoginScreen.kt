package com.example.moodii.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults // Make sure this import is correct
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.auth.login.LoginViewModel
import com.example.moodii.ui.auth.login.LoginState
import com.example.moodii.ui.components.AppBackground
import com.example.moodii.ui.components.AppLogo
import com.example.moodii.ui.components.LogoSize
import com.example.moodii.ui.navigation.AppDestinations
import com.example.moodii.ui.theme.PixelatedAppTheme // Ensure your custom theme is imported

@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Observe the login state from ViewModel
    val loginState by viewModel.loginState.collectAsState()
    
    // Local state for validation messages
    var validationMessage by remember { mutableStateOf<String?>(null) }
    
    // Handle navigation and messages based on login state
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate(AppDestinations.DASHBOARD_ROUTE) {
                    // Clear the back stack so user can't navigate back to login
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            }
            else -> { /* Handle other states in the UI */ }
        }
    }

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp) // Constrain max width for larger screens
                .background(
                    MaterialTheme.colorScheme.surface, // Uses surface color (e.g., FloralWhite) from your theme
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    4.dp,
                    MaterialTheme.colorScheme.primary, // Uses primary color (e.g., DarkViolet) from your theme
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo at 1/3 width of login box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AppLogo(
                    size = LogoSize.Medium,
                    showText = false
                )
            }
            
            Text(
                text = "LOGIN",
                fontSize = 18.sp,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily, // Apply your pixelated font
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, // Uses onSurface color (e.g., Indigo) from your theme
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        "Username or Email",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily, // Apply your pixelated font to label
                        color = if (username.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface // Adjust label color
                    )
                },
                // Placeholder is usually only visible when label is not active and input is empty.
                // If you use `label`, `placeholder` might not be strictly necessary depending on desired behavior.
                // placeholder = {
                //     Text(
                //         "Username or Email",
                //         fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                //         color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                //     )
                // },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Container (background) colors
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface, // If you ever disable it

                    // Border colors
                    focusedBorderColor = MaterialTheme.colorScheme.primary, // DarkViolet
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary, // MediumPurple
                    errorBorderColor = MaterialTheme.colorScheme.error, // Red
                    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),

                    // Text (input) colors
                    focusedTextColor = MaterialTheme.colorScheme.onSurface, // Indigo
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // Indigo
                    errorTextColor = MaterialTheme.colorScheme.error, // Red
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    // Label colors
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // DarkViolet when focused
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Indigo, faded when unfocused
                    errorLabelColor = MaterialTheme.colorScheme.error, // Red

                    // Placeholder colors
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    // Cursor colors
                    cursorColor = MaterialTheme.colorScheme.primary, // DarkViolet
                    errorCursorColor = MaterialTheme.colorScheme.error, // Red
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface // Ensure text color is from theme
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Password",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        color = if (password.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Container (background) colors
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    // Border colors
                    focusedBorderColor = MaterialTheme.colorScheme.primary, // DarkViolet
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary, // MediumPurple
                    errorBorderColor = MaterialTheme.colorScheme.error, // Red
                    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),

                    // Text (input) colors
                    focusedTextColor = MaterialTheme.colorScheme.onSurface, // Indigo
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // Indigo
                    errorTextColor = MaterialTheme.colorScheme.error, // Red
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    // Label colors
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // DarkViolet when focused
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Indigo, faded when unfocused
                    errorLabelColor = MaterialTheme.colorScheme.error, // Red

                    // Placeholder colors
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    // Cursor colors
                    cursorColor = MaterialTheme.colorScheme.primary, // DarkViolet
                    errorCursorColor = MaterialTheme.colorScheme.error, // Red
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Button(
                onClick = {
                    validationMessage = when {
                        username.isBlank() || password.isBlank() -> "Please fill in both fields."
                        else -> {
                            viewModel.login(username, password)
                            null // Clear validation message when submitting
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                enabled = loginState !is LoginState.Loading, // Disable button while loading
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDA70D6), // Orchid (kept hardcoded as it's a specific button color)
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (loginState is LoginState.Loading) "LOGGING IN..." else "LOGIN",
                    fontSize = 14.sp,
                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily, // Apply your pixelated font
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Don't have an account? Sign Up",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary, // Uses secondary color (e.g., MediumPurple) from your theme
                fontFamily = MaterialTheme.typography.bodySmall.fontFamily, // Apply your pixelated font
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { navController.navigate(AppDestinations.SIGNUP_ROUTE) } // Navigate to signup screen
            )
//
//            // Test Login Button (for development)
//            Button(
//                onClick = {
//                    viewModel.login("testuser", "password123")
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF32CD32), // LimeGreen for test button
//                    contentColor = Color.White
//                ),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text(
//                    text = "TEST LOGIN",
//                    fontSize = 12.sp,
//                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
//                    fontWeight = FontWeight.Bold
//                )
//            }

            // Message Box - Show validation errors or login errors
            val errorMessage = when {
                validationMessage != null -> validationMessage
                loginState is LoginState.Error -> (loginState as LoginState.Error).message
                else -> null
            }
            
            if (errorMessage != null) {
                AlertDialog(
                    onDismissRequest = { 
                        validationMessage = null
                        viewModel.clearError()
                    },
                    confirmButton = {
                        TextButton(onClick = { 
                            validationMessage = null
                            viewModel.clearError()
                        }) {
                            Text(
                                "OK",
                                color = MaterialTheme.colorScheme.onPrimary, // White
                                fontFamily = MaterialTheme.typography.labelSmall.fontFamily
                            )
                        }
                    },
                    title = {
                        Text(
                            if (validationMessage != null) "Validation Error" else "Login Error",
                            fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    text = {
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.onSurface, // Indigo from theme
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface, // FloralWhite from theme
                    shape = RoundedCornerShape(8.dp) // Apply a shape
                )
            }
        }
    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PixelLoginScreenPreview() {
    PixelatedAppTheme { // Wrap in your app theme for preview
        LoginScreen(navController = rememberNavController())
    }
}