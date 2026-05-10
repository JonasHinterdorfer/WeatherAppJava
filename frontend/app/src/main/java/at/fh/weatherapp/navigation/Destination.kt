package at.fh.weatherapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Destination : NavKey {

    @Serializable
    data object CityList : Destination()

    @Serializable
    data class WeatherDetail(val cityId: Long, val cityName: String) : Destination()

    @Serializable
    data object UserSettings : Destination()
}
