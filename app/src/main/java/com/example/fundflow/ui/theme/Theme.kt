package com.example.fundflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = PrimaryLime,
    onPrimary        = TextDark,
    primaryContainer = HeaderGreen,
    onPrimaryContainer = TextDark,

    secondary        = IuranBlue,
    onSecondary      = CardWhite,
    secondaryContainer = IuranBlue.copy(alpha = 0.12f),
    onSecondaryContainer = IuranBlue,

    tertiary         = ReportOrange,
    onTertiary       = CardWhite,

    background       = AppBackground,
    onBackground     = TextDark,

    surface          = CardWhite,
    onSurface        = TextDark,
    surfaceVariant   = SurfaceGray,
    onSurfaceVariant = TextLight,

    outline          = BorderGray,
    outlineVariant   = ChipUnselected,

    error            = ExpenseRed,
    onError          = CardWhite,
    errorContainer   = ExpenseRed.copy(alpha = 0.12f),
    onErrorContainer = ExpenseRed,

    inverseSurface   = NavBackground,
    inverseOnSurface = CardWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary          = PrimaryLime,
    onPrimary        = TextDark,
    primaryContainer = PrimaryLimeDark,
    onPrimaryContainer = DarkTextPrimary,

    secondary        = IuranBlue,
    onSecondary      = DarkTextPrimary,
    secondaryContainer = IuranBlue.copy(alpha = 0.20f),
    onSecondaryContainer = IuranBlue,

    tertiary         = ReportOrange,
    onTertiary       = DarkTextPrimary,

    background       = DarkBackground,
    onBackground     = DarkTextPrimary,

    surface          = DarkSurface,
    onSurface        = DarkTextPrimary,
    surfaceVariant   = DarkCard,
    onSurfaceVariant = DarkTextSecondary,

    outline          = DarkBorder,
    outlineVariant   = DarkBorder,

    error            = ExpenseRed,
    onError          = DarkTextPrimary,
    errorContainer   = ExpenseRed.copy(alpha = 0.20f),
    onErrorContainer = ExpenseRed,

    inverseSurface   = DarkTextPrimary,
    inverseOnSurface = DarkBackground,
)

@Composable
fun FundFlowTheme(
    darkTheme: Boolean = false,
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
