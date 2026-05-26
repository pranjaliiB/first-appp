package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class EmotionalThemeState {
    PEACEFUL,   // Positive days
    SHARP_WARN  // Unproductive days / realistic check
}

private val PeacefulColorScheme = darkColorScheme(
    primary = HarmoniousPrimary,
    secondary = HarmoniousSecondary,
    tertiary = HarmoniousGlow,
    background = BackgroundDark,
    surface = CardBackgroundDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = OffWhite,
    onSurface = OffWhite,
    error = WarningGlow
)

private val SharpWarnColorScheme = darkColorScheme(
    primary = WarningGlow,
    secondary = WarningSecondary,
    tertiary = WarningGlow,
    background = WarningPrimary,
    surface = WarningSecondary,
    onPrimary = Color.White,
    onSecondary = GreyText,
    onTertiary = Color.White,
    onBackground = OffWhite,
    onSurface = OffWhite,
    error = WarningGlow
)

private val LightColorScheme = lightColorScheme(
    primary = HarmoniousPrimary,
    secondary = HarmoniousSecondary,
    tertiary = HarmoniousGlow,
    background = Color(0xFFF0F2FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF10121C),
    onSurface = Color(0xFF10121C),
    error = WarningGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    emotionalState: EmotionalThemeState = EmotionalThemeState.PEACEFUL,
    content: @Composable () -> Unit
) {
    val colorScheme = if (!darkTheme) {
        LightColorScheme
    } else {
        when (emotionalState) {
            EmotionalThemeState.PEACEFUL -> PeacefulColorScheme
            EmotionalThemeState.SHARP_WARN -> SharpWarnColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
