package at.fh.weatherapp.data.network

import at.fh.weatherapp.data.model.City
import at.fh.weatherapp.data.model.WeatherDetail
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApiService {

    @GET("cities")
    suspend fun getCities(): List<City>

    @GET("weather/{cityId}")
    suspend fun getWeather(@Path("cityId") cityId: Long): WeatherDetail
}
