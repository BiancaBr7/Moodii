package com.example.moodii.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodii.R
import com.example.moodii.ui.theme.*

/**
 * Background component that provides a consistent background image/gradient
 * across the application with optional overlay
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    showOverlay: Boolean = true,
    overlayAlpha: Float = 0.7f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient (fallback if no image)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AudioRecorderBackgroundPage,
                            PixelatedPurpleMedium.copy(alpha = 0.8f),
                            PixelatedPurpleDarker.copy(alpha = 0.6f)
                        )
                    )
                )
        )
        
        // Background image
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f), // Increased alpha to better show your custom background
            contentScale = ContentScale.Crop
        )
        
        // Overlay for better text readability
        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = overlayAlpha * 0.3f)
                    )
            )
        }
        
        // Content
        content()
    }
}

/**
 * App logo component that can be used throughout the application
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: LogoSize = LogoSize.Medium,
    showText: Boolean = true
) {
    val logoSizeDp = when (size) {
        LogoSize.Small -> 48.dp
        LogoSize.Medium -> 80.dp
        LogoSize.Large -> 120.dp
        LogoSize.ExtraLarge -> 160.dp
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo image or icon
        Card(
            modifier = Modifier.size(logoSizeDp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PixelatedPurpleMedium
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Use the custom logo
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Moodii Logo",
                    modifier = Modifier.size(logoSizeDp * 0.8f),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        if (showText) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MOODII",
                fontSize = when (size) {
                    LogoSize.Small -> 12.sp
                    LogoSize.Medium -> 16.sp
                    LogoSize.Large -> 20.sp
                    LogoSize.ExtraLarge -> 24.sp
                },
                fontFamily = PressStart2P,
                color = AudioRecorderTextPrimary,
                letterSpacing = 2.sp
            )
        }
    }
}

enum class LogoSize {
    Small, Medium, Large, ExtraLarge
}

/**
 * Pixelated pattern overlay for retro gaming aesthetic
 */
@Composable
fun PixelatedOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.1f
) {
    // This creates a subtle pixelated pattern effect
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        PixelatedPurpleDarker.copy(alpha = alpha),
                        Color.Transparent
                    ),
                    radius = 200f
                )
            )
    )
}

@Preview(showBackground = true)
@Composable
fun AppBackgroundPreview() {
    PixelatedAppTheme {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppLogo(size = LogoSize.Large)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Welcome to Moodii",
                    fontSize = 18.sp,
                    fontFamily = PressStart2P,
                    color = AudioRecorderTextPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogoPreview() {
    PixelatedAppTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppLogo(size = LogoSize.Small)
            AppLogo(size = LogoSize.Medium)
            AppLogo(size = LogoSize.Large)
        }
    }
}
