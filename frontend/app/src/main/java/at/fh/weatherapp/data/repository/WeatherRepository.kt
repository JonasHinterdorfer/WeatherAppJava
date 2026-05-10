package at.fh.weatherapp.data.repository

import at.fh.weatherapp.data.model.City
import at.fh.weatherapp.data.model.WeatherDetail
import at.fh.weatherapp.data.network.RetrofitClient

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class WeatherRepository {

    private val api = RetrofitClient.apiService

    suspend fun getCities(): Result<List<City>> {
        return try {
            val cities = api.getCities()
            Result.Success(cities)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error fetching cities")
        }
    }

    suspend fun getWeather(cityId: Long): Result<WeatherDetail> {
        return try {
            val weather = api.getWeather(cityId)
            Result.Success(weather)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error fetching weather")
        }
    }
}
