package com.stardust.cetccalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OneUIBlue,
    onPrimary = Color.White,
    secondary = OneUIBlue,
    onSecondary = Color.White,
    background = OneUIBackground,
    onBackground = OneUITextPrimary,
    surface = OneUISurface,
    onSurface = OneUITextPrimary,
    surfaceVariant = OneUIBackground,
    onSurfaceVariant = OneUITextSecondary,
    outline = OneUIOutline,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun CETCTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
