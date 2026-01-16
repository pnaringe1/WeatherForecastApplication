package com.example.weatherforecastapp.domain.repository

import com.example.weatherforecastapp.domain.model.WeatherForecast

/**
 * Repository interface for weather data operations
 * Following Repository pattern and Dependency Inversion Principle
 */
interface WeatherRepository {
    /**
     * Fetches 5-day weather forecast for given coordinates
     * @param latitude Geographic latitude
     * @param longitude Geographic longitude
     * @return Result containing list of WeatherForecast or error
     */
    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<WeatherForecast>>
}
