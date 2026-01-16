package com.example.weatherforecastapp.data.remote

import com.example.weatherforecastapp.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for OpenWeatherMap
 */
interface WeatherApiService {
    
    /**
     * Fetches 5-day weather forecast
     * @param latitude Geographic latitude
     * @param longitude Geographic longitude
     * @param apiKey API key for OpenWeatherMap
     * @param units Temperature unit (metric for Celsius)
     * @return WeatherResponse containing forecast data
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}
