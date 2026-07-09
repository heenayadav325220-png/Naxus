package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimarySpace,
    secondary = SecondarySpace,
    tertiary = TertiarySpace,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    surfaceVariant = SpaceCardBg,
    onSurfaceVariant = StarlightWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimarySpace,
    secondary = SecondarySpace,
    tertiary = TertiarySpace,
    background = Color(0xFFF6F5FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1B1B1F),
    onSurface = Color(0xFF1B1B1F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark space theme by default as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
