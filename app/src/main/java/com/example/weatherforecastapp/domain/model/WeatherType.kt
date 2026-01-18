package com.example.weatherforecastapp.domain.model

import com.example.weatherforecastapp.R


/**
 * Represents the different weather conditions
 */
enum class WeatherType {
    SUNNY,
    RAINY,
    CLOUDY;

    fun getBackgroundResource(): Int {
        return when (this) {
            SUNNY -> R.drawable.sunny
            RAINY -> R.drawable.rainy
            CLOUDY -> R.drawable.cloudy
        }
    }

    companion object {
        fun fromCondition(condition: String): WeatherType {
            return when (condition.lowercase()) {
                "clear" -> SUNNY
                "rain", "drizzle", "thunderstorm" -> RAINY
                "clouds", "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> CLOUDY
                "snow" -> CLOUDY // Treat snow as cloudy
                else -> CLOUDY // Default to cloudy for unknown conditions
            }
        }
    }
}
