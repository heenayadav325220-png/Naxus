package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.random.Random

// --- Star Model for Twinkling background ---
private data class Star(
    val xPercent: Float,
    val yPercent: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)

@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Generate a fixed set of stars
    val stars = remember {
        List(120) {
            Star(
                xPercent = Random.nextFloat(),
                yPercent = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 1.5f + 0.5f,
                phase = Random.nextFloat() * 10f
            )
        }
    }

    // Twinkling animation ticker
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val animState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteSpec(),
        label = "angle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepSpaceBlack,
                        CosmicDarkBlue,
                        DeepSpaceBlack
                    )
                )
            )
    ) {
        // Render the starry background canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Draw a subtle, massive, glowing nebular gas cloud in the center
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2E7200FF), // Soft nebular purple
                                Color(0x00000000)
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.4f),
                            radius = size.width * 0.8f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x1F00D2FF), // Soft nebular blue
                                Color(0x00000000)
                            ),
                            center = Offset(size.width * 0.2f, size.height * 0.7f),
                            radius = size.width * 0.6f
                        )
                    )
                }
        ) {
            // Draw all stars with twinkling phase offset
            for (star in stars) {
                val alpha = (0.3f + 0.7f * kotlin.math.sin(animState.value * star.speed + star.phase)).coerceIn(0.1f, 1.0f)
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = star.size,
                    center = Offset(star.xPercent * size.width, star.yPercent * size.height)
                )
            }
        }

        // Render main children
        content()
    }
}

private fun infiniteSpec(): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(15000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = SpaceCardStroke,
    backgroundColor: Color = SpaceCardBg,
    testTag: String = "glass_card",
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var cardModifier = modifier
        .testTag(testTag)
        .graphicsLayer {
            clip = true
            shape = RoundedCornerShape(cornerRadius)
        }
        .background(
            Brush.linearGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.25f),
                    backgroundColor.copy(alpha = 0.08f)
                ),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        )
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor.copy(alpha = 0.45f),
                    borderColor.copy(alpha = 0.12f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )

    if (onClick != null) {
        cardModifier = cardModifier.clickable(onClick = onClick)
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    testTag: String = "glass_textfield"
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = StarlightWhite.copy(alpha = 0.5f)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        modifier = modifier
            .testTag(testTag)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SpaceCardStroke.copy(alpha = 0.4f),
                        SpaceCardStroke.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SpaceCardBg.copy(alpha = 0.15f),
            unfocusedContainerColor = SpaceCardBg.copy(alpha = 0.15f),
            focusedTextColor = StarlightWhite,
            unfocusedTextColor = StarlightWhite,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = NebulaBlue
        )
    )
}

@Composable
fun GlassmorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = StarlightWhite
    ),
    testTag: String = "glass_button",
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .testTag(testTag)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimarySpace.copy(alpha = 0.85f),
                        SecondarySpace.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = colors,
        content = content
    )
}
