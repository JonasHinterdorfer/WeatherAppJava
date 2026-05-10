package at.fh.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fh.weatherapp.data.model.WeatherDetail
import at.fh.weatherapp.ui.theme.AccentBlue
import at.fh.weatherapp.ui.theme.AccentCyan
import at.fh.weatherapp.ui.theme.Navy700
import at.fh.weatherapp.ui.theme.Navy800
import at.fh.weatherapp.ui.theme.Navy900
import at.fh.weatherapp.ui.theme.OnSurfaceVariant
import at.fh.weatherapp.ui.theme.SurfaceCard
import at.fh.weatherapp.viewmodel.WeatherDetailViewModel
import kotlin.math.roundToInt

@Composable
fun WeatherDetailScreen(
    cityId: Long,
    cityName: String,
    onBack: () -> Unit
) {
    val viewModel: WeatherDetailViewModel = viewModel(
        factory = WeatherDetailViewModel.Factory(cityId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Navy800, Navy900),
                    startY = 0f,
                    endY = 1200f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            DetailTopBar(cityName = cityName, onBack = onBack, onRefresh = viewModel::fetchWeather)

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AccentBlue, strokeWidth = 3.dp)
                            Spacer(Modifier.height(16.dp))
                            Text("Fetching weather...", color = OnSurfaceVariant, fontSize = 14.sp)
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    DetailErrorContent(
                        message = uiState.errorMessage ?: "Unknown error",
                        onRetry = viewModel::fetchWeather
                    )
                }
                uiState.weather != null -> {
                    WeatherContent(weather = uiState.weather!!)
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(cityName: String, onBack: () -> Unit, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = cityName,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = AccentBlue
            )
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Large temperature display
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.3f),
                            Navy700
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = weatherEmoji(weather.description),
                    fontSize = 36.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${weather.temperature.roundToInt()}°",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            fontSize = 18.sp,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        // Exact temperature with decimal
        Text(
            text = "${"%.1f".format(weather.temperature)} °C",
            fontSize = 14.sp,
            color = AccentCyan,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.height(40.dp))

        // Stats grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Thermostat,
                label = "Temperature",
                value = "${"%.1f".format(weather.temperature)} °C",
                iconTint = AccentBlue
            )

            weather.humidity?.let {
                StatCard(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "$it %",
                    iconTint = AccentCyan
                )
            }

            weather.windSpeed?.let {
                StatCard(
                    icon = Icons.Default.Air,
                    label = "Wind Speed",
                    value = "${"%.1f".format(it)} km/h",
                    iconTint = Color(0xFF7B9FE8)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DetailErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                color = OnSurfaceVariant,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Again", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun weatherEmoji(description: String): String {
    val d = description.lowercase()
    return when {
        "sun" in d || "clear" in d -> "☀️"
        "cloud" in d && "partly" in d -> "⛅"
        "cloud" in d || "overcast" in d -> "☁️"
        "rain" in d || "shower" in d -> "🌧️"
        "thunder" in d || "storm" in d -> "⛈️"
        "snow" in d || "blizzard" in d -> "❄️"
        "fog" in d || "mist" in d -> "🌫️"
        "wind" in d -> "💨"
        "hot" in d || "warm" in d -> "🌤️"
        "cold" in d || "cool" in d -> "🌨️"
        else -> "🌡️"
    }
}
