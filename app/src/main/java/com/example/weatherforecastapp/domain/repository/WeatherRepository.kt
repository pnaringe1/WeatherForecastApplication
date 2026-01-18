package com.example.weatherforecastapp.domain.repository

import com.example.weatherforecastapp.domain.model.WeatherForecast

/**
 * Repository interface for weather data operations
 */
interface WeatherRepository {

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<WeatherForecast>>
}
