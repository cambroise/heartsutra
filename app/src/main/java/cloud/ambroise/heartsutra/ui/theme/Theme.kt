package cloud.ambroise.heartsutra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SealVermilion,
    onPrimary = PaperSurface,
    secondary = InkMuted,
    onSecondary = PaperSurface,
    background = PaperLight,
    onBackground = InkSumi,
    surface = PaperSurface,
    onSurface = InkSumi,
    surfaceVariant = PaperSurfaceHigh,
    onSurfaceVariant = InkMuted,
    surfaceContainerLowest = PaperSurface,
    surfaceContainer = PaperLight,
    surfaceContainerHigh = PaperSurfaceHigh,
    outlineVariant = PaperSurfaceHigh,
)

private val DarkColors = darkColorScheme(
    primary = SealVermilionDark,
    onPrimary = PaperNight,
    secondary = InkPaperMuted,
    onSecondary = PaperNight,
    background = PaperNight,
    onBackground = InkPaper,
    surface = PaperNightSurface,
    onSurface = InkPaper,
    surfaceVariant = PaperNightSurfaceHigh,
    onSurfaceVariant = InkPaperMuted,
    surfaceContainerLowest = PaperNightSurface,
    surfaceContainer = PaperNight,
    surfaceContainerHigh = PaperNightSurfaceHigh,
    outlineVariant = PaperNightSurfaceHigh,
)

@Composable
fun HeartSutraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
