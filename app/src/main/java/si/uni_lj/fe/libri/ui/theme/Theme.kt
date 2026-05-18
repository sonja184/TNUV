package si.uni_lj.fe.libri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrightGreen,
    secondary = SoftOrange,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = Color.White,
    onSecondary = TextDark,

    onBackground = CreamText,
    onSurface = CreamText,

    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    secondary = WarmOrange,

    background = CreamBackground,
    surface = CardCream,

    onPrimary = Color.White,
    onSecondary = TextDark,

    onBackground = TextDark,
    onSurface = TextDark,

    error = ErrorRed
)

@Composable
fun LibriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}