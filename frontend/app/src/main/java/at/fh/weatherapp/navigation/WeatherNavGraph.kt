package at.fh.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import at.fh.weatherapp.ui.screens.CityListScreen
import at.fh.weatherapp.ui.screens.WeatherDetailScreen

@Composable
fun WeatherNavGraph() {
    val backStack = rememberNavBackStack(Destination.CityList)

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
        }
    )
}
