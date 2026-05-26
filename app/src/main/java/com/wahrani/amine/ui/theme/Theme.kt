package com.wahrani.amine.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WahraniRed,
    secondary = WahraniRed,
    tertiary = WahraniRed,
    background = AlmostBlack,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onPrimary = AlmostBlack,
    onSecondary = AlmostBlack,
    onTertiary = AlmostBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = CategoryChipBg,
    error = ErrorRed
)

@Composable
fun WahraniAmineTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
