package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CosmicDarkColorScheme = darkColorScheme(
    primary = CyanCosmic,
    onPrimary = Color(0xFF001E26),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFFB8F5FF),
    secondary = OrangeCosmic,
    onSecondary = Color(0xFF421E00),
    secondaryContainer = Color(0xFF6B3300),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = PurpleQuasar,
    onTertiary = Color(0xFF2B0068),
    background = SpaceBlack,
    onBackground = StellarWhite,
    surface = SpaceCardBg,
    onSurface = StellarWhite,
    surfaceVariant = SpaceCardElevated,
    onSurfaceVariant = TextMuted,
    outline = SpaceBorder,
    outlineVariant = Color(0xFF2C2C46)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The Light Universe app strictly employs the eye-protecting deep space dark aesthetic
    MaterialTheme(
        colorScheme = CosmicDarkColorScheme,
        typography = Typography,
        content = content
    )
}
