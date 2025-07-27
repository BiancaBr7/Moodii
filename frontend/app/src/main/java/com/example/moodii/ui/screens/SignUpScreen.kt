package com.example.moodii.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
// import androidx.compose.material3.TextFieldDefaults // Remove this import if it exists
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
import com.example.moodii.ui.auth.register.RegisterViewModel
import com.example.moodii.ui.auth.register.RegisterState
import com.example.moodii.ui.components.AppBackground
import com.example.moodii.ui.components.AppLogo
import com.example.moodii.ui.components.LogoSize
import com.example.moodii.ui.navigation.AppDestinations
import com.example.moodii.ui.theme.PixelatedAppTheme // Ensure your custom theme is imported

@Composable
fun SignUpScreen(navController: NavHostController, viewModel: RegisterViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Observe the register state from ViewModel
    val registerState by viewModel.registerState.collectAsState()
    
    // Local state for validation messages
    var validationMessage by remember { mutableStateOf<String?>(null) }
    
    // Handle navigation and messages based on register state
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                navController.navigate(AppDestinations.LOGIN_ROUTE) {
                    // Clear the back stack so user can't navigate back to signup after successful registration
                    popUpTo(AppDestinations.SIGNUP_ROUTE) { inclusive = true }
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
            // Logo at 1/3 width of signup box
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
                text = "REGISTER",
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
                        "Username",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        color = if (username.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors( // Correct function
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        "Email",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        color = if (email.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors( // Correct function
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    errorLabelColor = MaterialTheme.colorScheme.error,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface
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
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors( // Correct function
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    errorLabelColor = MaterialTheme.colorScheme.error,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(
                        "Confirm Password",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        color = if (confirmPassword.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors( // Correct function
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    errorLabelColor = MaterialTheme.colorScheme.error,

                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Button(
                onClick = {
                    validationMessage = when {
                        username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                            "Please fill in all fields."
                        password != confirmPassword ->
                            "Passwords do not match."
                        email.contains("@").not() || email.contains(".").not() ->
                            "Please enter a valid email address."
                        password.length < 6 ->
                            "Password must be at least 6 characters long."
                        else -> {
                            viewModel.register(username, email, password)
                            null // Clear validation message when submitting
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                enabled = registerState !is RegisterState.Loading, // Disable button while loading
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDA70D6), // Orchid (kept hardcoded as it's a specific button color)
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (registerState is RegisterState.Loading) "REGISTERING..." else "REGISTER",
                    fontSize = 14.sp,
                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Already have an account? Log In",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary, // MediumPurple from theme
                fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        navController.navigate(AppDestinations.LOGIN_ROUTE) // Navigate to login screen
                    }
            )

            // Message Box - Show validation errors, registration errors, or success messages
            val errorMessage = when {
                validationMessage != null -> validationMessage
                registerState is RegisterState.Error -> (registerState as RegisterState.Error).message
                else -> null
            }
            
            val successMessage = when {
                registerState is RegisterState.Success -> (registerState as RegisterState.Success).message
                else -> null
            }
            
            // Show error dialog
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
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = MaterialTheme.typography.labelSmall.fontFamily
                            )
                        }
                    },
                    title = {
                        Text(
                            if (validationMessage != null) "Validation Error" else "Registration Error",
                            fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    text = {
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            
            // Show success dialog
            if (successMessage != null) {
                AlertDialog(
                    onDismissRequest = { /* Auto-navigate handled in LaunchedEffect */ },
                    confirmButton = {
                        TextButton(onClick = { 
                            navController.navigate(AppDestinations.LOGIN_ROUTE) {
                                popUpTo(AppDestinations.SIGNUP_ROUTE) { inclusive = true }
                            }
                        }) {
                            Text(
                                "Continue to Login",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = MaterialTheme.typography.labelSmall.fontFamily
                            )
                        }
                    },
                    title = {
                        Text(
                            "Registration Successful!",
                            fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Text(
                            successMessage,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PixelRegisterScreenPreview() {
    PixelatedAppTheme { // Wrap in your app theme for preview
        SignUpScreen(navController = rememberNavController())
    }
}