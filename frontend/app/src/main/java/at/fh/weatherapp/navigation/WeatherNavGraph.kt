package at.fh.weatherapp.navigation

import androidx.compose.material3.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import at.fh.weatherapp.ui.screens.CityListScreen
import at.fh.weatherapp.ui.screens.UserSettingsScreen
import at.fh.weatherapp.ui.screens.WeatherDetailScreen
import at.fh.weatherapp.viewmodel.UserViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherNavGraph(
    userViewModel: UserViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(Destination.CityList)
    val userData by userViewModel.userData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hello, ${userData?.firstName ?: "Guest"}") },
                actions = {
                    IconButton(onClick = { backStack.add(Destination.UserSettings) }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Destination.CityList> {
                        CityListScreen(
                            onCityClick = { city ->
                                backStack.add(
                                    Destination.WeatherDetail(
                                        cityId = city.id,
                                        cityName = city.name
                                    )
                                )
                            }
                        )
                    }

                    entry<Destination.WeatherDetail> { key ->
                        WeatherDetailScreen(
                            cityId = key.cityId,
                            cityName = key.cityName,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<Destination.UserSettings> {
                        UserSettingsScreen(
                            userViewModel = userViewModel,
                            onNavigateBack = { backStack.removeLastOrNull() }
                        )
                    }
                }
            )
        }
    }
}
