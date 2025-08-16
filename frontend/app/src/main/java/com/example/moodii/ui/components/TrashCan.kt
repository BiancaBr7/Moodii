package com.example.moodii.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moodii.R
import com.example.moodii.ui.theme.PixelatedAppTheme
import com.example.moodii.ui.theme.PixelatedPurpleDarker
import com.example.moodii.ui.theme.PixelatedPurpleFill
import com.example.moodii.ui.theme.PixelatedPurpleMedium

// Custom shape for pixelated shadow (simplified for illustration)
private val PixelatedShadowShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

@Composable
fun TrashCan(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RectangleShape, // Simplified shadow shape
                ambientColor = PixelatedPurpleDarker,
                spotColor = PixelatedPurpleDarker
            )
            .background(PixelatedPurpleMedium, RectangleShape)
            .border(2.dp, PixelatedPurpleDarker, RectangleShape) // Add border for better visibility
            .clickable { 
                println("TrashCan: onClick triggered!")
                onClick() 
            }
            .padding(4.dp) // Inner padding to show border
    ) {
        // Here you would typically use an Image composable with a VectorDrawable
        // representing your pixelated trash can SVG.
        // For demonstration, let's just use a placeholder background.
        // You'll need to create a Vector Drawable for the trash can (e.g., ic_trash_can.xml)
        // in your drawable folder.
        // Example: res/drawable/ic_trash_can.xml
        /*
        <vector xmlns:android="http://schemas.android.com/apk/res/android"
            android:width="24dp"
            android:height="24dp"
            android:viewportWidth="24"
            android:viewportHeight="24">
            <rect android:fill="#A78AD0" android:pathData="M5 4H19V6H5Z"/>
            <rect android:fill="#A78AD0" android:pathData="M8 6H16V7H8Z"/>
            <path android:fill="#C2A7E0" android:pathData="M6 7L6 20L18 20L18 7Z"/>
            <line android:stroke="#5A3D7A" android:strokeWidth="2" android:strokeLineCap="butt" android:strokeLineJoin="miter" android:x1="10" android:y1="9" android:x2="10" android:y2="18"/>
            <line android:stroke="#5A3D7A" android:strokeWidth="2" android:strokeLineCap="butt" android:strokeLineJoin="miter" android:x1="14" android:y1="9" android:x2="14" android:y2="18"/>
        </vector>
        */
        Image(
            painter = painterResource(id = R.drawable.ic_trash_can), // Replace with your actual drawable
            contentDescription = "Delete",
            modifier = Modifier
                .size(32.dp) // Adjust size as needed, this will be scaled
                .background(PixelatedPurpleFill) // Example background if not using a detailed drawable
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PixelatedGarbageCanPreview() {
    PixelatedAppTheme {
        TrashCan()
    }
}