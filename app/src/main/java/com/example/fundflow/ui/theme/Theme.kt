package com.example.fundflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Green500,
    onPrimary        = White,
    primaryContainer = Green100,
    onPrimaryContainer = Green700,

    secondary        = Green400,
    onSecondary      = White,
    secondaryContainer = Green50,
    onSecondaryContainer = Green600,

    error            = Red500,
    onError          = White,
    errorContainer   = Red100,
    onErrorContainer = Red500,

    background       = Gray50,
    onBackground     = Gray900,

    surface          = White,
    onSurface        = Gray900,
    surfaceVariant   = Gray100,
    onSurfaceVariant = Gray600,

    outline          = Gray300,
    outlineVariant   = Gray200,
)

private val DarkColorScheme = darkColorScheme(
    primary          = Green400,
    onPrimary        = Gray900,
    primaryContainer = Green700,
    onPrimaryContainer = Green100,

    secondary        = Green400,
    onSecondary      = Gray900,
    secondaryContainer = Green700,
    onSecondaryContainer = Green100,

    error            = Red400,
    onError          = Gray900,
    errorContainer   = Color(0xFF5C1A1A),
    onErrorContainer = Red400,

    background       = DarkBackground,
    onBackground     = Gray100,

    surface          = DarkSurface,
    onSurface        = Gray100,
    surfaceVariant   = DarkSurface2,
    onSurfaceVariant = Gray400,

    outline          = Gray700,
    outlineVariant   = Gray800,
)

@Composable
fun FundFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FundFlowTypography,
        shapes      = FundFlowShapes,
        content     = content
    )
}
