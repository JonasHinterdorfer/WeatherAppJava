package at.fh.weatherapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fh.weatherapp.data.model.City
import at.fh.weatherapp.ui.theme.AccentBlue
import at.fh.weatherapp.ui.theme.AccentCyan
import at.fh.weatherapp.ui.theme.Navy700
import at.fh.weatherapp.ui.theme.Navy800
import at.fh.weatherapp.ui.theme.Navy900
import at.fh.weatherapp.ui.theme.OnSurfaceVariant
import at.fh.weatherapp.ui.theme.SurfaceCard
import at.fh.weatherapp.viewmodel.CityListViewModel

@Composable
fun CityListScreen(
    onCityClick: (City) -> Unit,
    viewModel: CityListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissError()
        }
    }

    Scaffold(
        containerColor = Navy900,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SurfaceCard,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = AccentCyan,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Navy800, Navy900),
                        startY = 0f,
                        endY = 800f
                    )
                )
                .padding(paddingValues)
        ) {
            // Header
            CityListHeader()

            when {
                uiState.isLoading && uiState.cities.isEmpty() -> {
                    LoadingContent()
                }
                !uiState.isLoading && uiState.cities.isEmpty() && uiState.errorMessage == null -> {
                    ErrorContent(
                        message = "Could not connect to server",
                        onRetry = viewModel::retry
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(uiState.cities) { index, city ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(300, delayMillis = index * 60)) +
                                        slideInVertically(
                                            animationSpec = tween(300, delayMillis = index * 60),
                                            initialOffsetY = { it / 2 }
                                        )
                            ) {
                                CityCard(city = city, onClick = { onCityClick(city) })
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityListHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AccentBlue, AccentCyan))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Weather",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Select a city to view current conditions",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun CityCard(city: City, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Navy700),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cityEmoji(city.name),
                        fontSize = 22.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = city.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = city.country,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View weather",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentBlue, strokeWidth = 3.dp)
            Spacer(Modifier.height(16.dp))
            Text("Loading cities...", color = OnSurfaceVariant, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
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
                Text("Retry", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun cityEmoji(cityName: String): String = when (cityName.lowercase()) {
    "berlin" -> "🇩🇪"
    "wien", "vienna" -> "🇦🇹"
    "zürich", "zurich" -> "🇨🇭"
    "paris" -> "🇫🇷"
    "london" -> "🇬🇧"
    "amsterdam" -> "🇳🇱"
    "rome", "roma" -> "🇮🇹"
    "madrid" -> "🇪🇸"
    else -> "🌍"
}
