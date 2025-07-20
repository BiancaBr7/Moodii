package com.example.moodii.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moodii.R

@Composable
fun LogListScreen(navController: NavHostController) {
    val pixelFont = FontFamily(Font(R.font.press_start_2p))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2BBF1)), // #d2bbf1
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(16.dp)
                .background(Color(0xFFE5D3F8), RoundedCornerShape(8.dp))
                .border(2.dp, Color(0xFF5A3D7A), RoundedCornerShape(8.dp))
                .padding(4.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp)),
        ) {
            // Top bar with control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC2A7E0))
                    .height(24.dp)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFF0F0F0), shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFF0F0F0), shape = CircleShape)
                    )
                }
                Text(
                    text = "X",
                    fontSize = 8.sp,
                    color = Color(0xFF5A3D7A),
                    fontFamily = pixelFont
                )
            }

            // Address bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(2.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ">",
                    fontSize = 8.sp,
                    color = Color(0xFF5A3D7A),
                    fontFamily = pixelFont,
                    modifier = Modifier.padding(start = 4.dp, end = 2.dp)
                )
                Text(
                    text = "https://fakewebpage.com/blog-post-title",
                    fontSize = 8.sp,
                    color = Color(0xFF5A3D7A),
                    maxLines = 1,
                    fontFamily = pixelFont
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("EMPTY BLOG POST", color = Color(0xFF5A3D7A), fontSize = 10.sp, fontFamily = pixelFont)
                Text("CLICK TO EDIT", color = Color(0xFF5A3D7A), fontSize = 10.sp, fontFamily = pixelFont)

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFC2A7E0), shape = RoundedCornerShape(4.dp))
                        .border(2.dp, Color(0xFF5A3D7A), shape = RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))
                val paragraphColor = Color(0xFF5A3D7A)
                val textStyle = LocalTextStyle.current.copy(
                    fontSize = 8.sp,
                    fontFamily = pixelFont,
                    color = paragraphColor,
                    lineHeight = 10.sp
                )

                Text("Lorem ipsum dolor sit amet,", style = textStyle)
                Text("consectetur adipiscing elit.", style = textStyle)
                Text("Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", style = textStyle)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Ut enim ad minim veniam,", style = textStyle)
                Text("quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", style = textStyle)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", style = textStyle)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Excepteur sint occaecat cupidatat non proident,", style = textStyle)
                Text("sunt in culpa qui officia deserunt mollit anim id est laborum.", style = textStyle)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PixelLogListScreenPreview() {
    // Optional: wrap in your app theme if needed
    LogListScreen(navController = rememberNavController())
}
