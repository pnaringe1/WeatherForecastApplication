package com.example.weatherforecastapp.domain.model

import java.time.LocalDate

/**
 * Domain model representing a single day's weather forecast
 */
data class WeatherForecast(
    val date: LocalDate,
    val dayOfWeek: String,
    val temperature: Double,
    val weatherType: WeatherType
) {
    /**
     * Returns formatted temperature string with degree symbol
     */
    fun getFormattedTemperature(): String {
        return "${temperature.toInt()}°"
    }
}
