package com.risket.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryColor = Color(0xFF1B1F3B)
private val AccentColor = Color(0xFF4CAF50)
private val WarnColor = Color(0xFFE53935)

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    secondary = AccentColor,
    error = WarnColor
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8C9EFF),
    secondary = AccentColor,
    error = WarnColor
)

@Composable
fun RisketTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
