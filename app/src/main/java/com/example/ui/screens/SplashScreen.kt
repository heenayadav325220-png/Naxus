package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SpaceBackground
import com.example.ui.theme.StarlightWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotation and pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    SpaceBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo in Center
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "StarNexus Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(CircleShape)
                    .testTag("splash_logo")
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Text: "StarNexus"
            Text(
                text = "StarNexus",
                fontSize = 38.sp,
                color = StarlightWhite,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.testTag("splash_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline: "Explore the Universe"
            Text(
                text = "Explore the Universe",
                fontSize = 16.sp,
                color = StarlightWhite.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.testTag("splash_tagline")
            )
        }
    }
}
