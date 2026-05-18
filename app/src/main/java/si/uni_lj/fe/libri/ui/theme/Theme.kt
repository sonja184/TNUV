package si.uni_lj.fe.libri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LibriGreen,
    onPrimary = Color.White,

    secondary = LibriOrange,
    onSecondary = LibriTextDark,

    tertiary = LibriGreenSoft,
    onTertiary = LibriGreenDark,

    background = LibriCream,
    onBackground = LibriTextDark,

    surface = LibriSurface,
    onSurface = LibriTextDark,

    surfaceVariant = LibriSurfaceWarm,
    onSurfaceVariant = LibriTextMuted,

    outline = LibriOutline,

    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = LibriOrange,
    onPrimary = LibriTextDark,

    secondary = LibriGreen,
    onSecondary = Color.White,

    tertiary = LibriDarkCard,
    onTertiary = LibriDarkText,

    background = LibriDarkBackground,
    onBackground = LibriDarkText,

    surface = LibriDarkSurface,
    onSurface = LibriDarkText,

    surfaceVariant = LibriDarkCard,
    onSurfaceVariant = LibriDarkMuted,

    outline = Color(0xFF33433D),

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun LibriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}