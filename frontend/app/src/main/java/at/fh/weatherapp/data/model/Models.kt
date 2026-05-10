package at.fh.weatherapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Long,
    val name: String,
    val country: String
)

@Serializable
data class WeatherDetail(
    val cityId: Long,
    val cityName: String,
    val temperature: Double,
    val description: String,
    val humidity: Int? = null,
    val windSpeed: Double? = null
)
