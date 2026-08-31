package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ManFarDarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = DarkObsidian,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldAccent,
    secondary = GoldPrimary,
    onSecondary = DarkObsidian,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = GoldAccent,
    tertiary = GoldAmberGlow,
    onTertiary = DarkObsidian,
    background = DarkObsidian,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSilver,
    outline = DarkBorder,
    outlineVariant = DarkSurfaceHighlight,
    error = StatusCancelled,
    onError = Color.White
)

private val ManFarLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = GoldAccent,
    onPrimaryContainer = DarkObsidian,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    background = CreamBackground,
    onBackground = DarkObsidian,
    surface = CreamSurface,
    onSurface = DarkObsidian,
    surfaceVariant = CreamBorder,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFFCBD5E1),
    error = StatusCancelled,
    onError = Color.White
)

@Composable
fun ManFarBarbershopTheme(
    darkTheme: Boolean = true, // Default to rich modern dark theme for barbershop aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ManFarDarkColorScheme else ManFarLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    ManFarBarbershopTheme(darkTheme = darkTheme, content = content)
}
