package at.fh.weatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// A clean, modern dark-blue palette
val Navy900 = Color(0xFF0A0F1E)
val Navy800 = Color(0xFF111827)
val Navy700 = Color(0xFF1A2540)
val Navy600 = Color(0xFF243050)
val AccentBlue = Color(0xFF4A90E2)
val AccentCyan = Color(0xFF00D4E0)
val SurfaceCard = Color(0xFF162035)
val OnSurface = Color(0xFFE8EDF5)
val OnSurfaceVariant = Color(0xFF8A9BB5)
val ErrorColor = Color(0xFFFF6B6B)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = Navy700,
    onPrimaryContainer = OnSurface,
    secondary = AccentCyan,
    onSecondary = Navy900,
    background = Navy900,
    onBackground = OnSurface,
    surface = Navy800,
    onSurface = OnSurface,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorColor,
    onError = Color.White,
    outline = Navy600
)

@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
