package com.adytransjaya.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryLight,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandTeal,
    onSecondary = Color.White,
    secondaryContainer = BrandTealLight,
    onSecondaryContainer = Color(0xFF04454C),
    tertiary = BrandAmber,
    onTertiary = Color(0xFF3A2900),
    tertiaryContainer = BrandAmberLight,
    onTertiaryContainer = Color(0xFF4A3400),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSoft,
    outlineVariant = Color(0xFFE3EBEF),
    error = Danger,
    onError = Color.White,
    errorContainer = DangerLight,
    onErrorContainer = Color(0xFF6B1717),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7CC6E0),
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF0A4358),
    onPrimaryContainer = Color(0xFFCDEAF5),
    secondary = Color(0xFF83D5DC),
    onSecondary = Color(0xFF00363B),
    secondaryContainer = Color(0xFF00505A),
    onSecondaryContainer = Color(0xFFB8EEF3),
    tertiary = Color(0xFFEBC46A),
    onTertiary = Color(0xFF3D2E00),
    tertiaryContainer = Color(0xFF574400),
    onTertiaryContainer = Color(0xFFFFE6A8),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF3D5A68),
    outlineVariant = Color(0xFF263F4B),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF691B1B),
    errorContainer = Color(0xFF8C2B2B),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun AdyTransJayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
