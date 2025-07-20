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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.ui.theme.PixelatedAppTheme // Ensure your custom theme is imported

@Composable
fun SignUpScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Uses background color from your PixelatedAppTheme
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
            Text(
                text = "SIGN UP",
                fontSize = 18.sp,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily, // Apply your pixelated font
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
                    message = when {
                        username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                            "Please fill in all fields."
                        password != confirmPassword ->
                            "Passwords do not match."
                        else -> {
                            // In a real app, you'd perform actual sign-up here
                            // For now, just navigate to login and show success message
                            navController.navigate("loginScreen")
                            "Sign up successful! Welcome, $username!"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDA70D6), // Orchid (kept hardcoded as it's a specific button color)
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "SIGN UP",
                    fontSize = 14.sp,
                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily
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
                        navController.navigate("loginScreen") // Navigate to login screen
                    }
            )

            if (message != null) {
                AlertDialog(
                    onDismissRequest = { message = null },
                    confirmButton = {
                        TextButton(onClick = { message = null }) {
                            Text(
                                "OK",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = MaterialTheme.typography.labelSmall.fontFamily
                            )
                        }
                    },
                    text = {
                        Text(
                            message!!,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PixelSignupScreenPreview() {
    PixelatedAppTheme { // Wrap in your app theme for preview
        SignUpScreen(navController = rememberNavController())
    }
}