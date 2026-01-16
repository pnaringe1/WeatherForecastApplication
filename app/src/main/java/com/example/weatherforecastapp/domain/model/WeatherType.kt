package com.example.weatherforecastapp.domain.model

import com.example.weatherforecastapp.R


/**
 * Represents the different weather conditions
 */
enum class WeatherType {
    SUNNY,
    RAINY,
    CLOUDY;

    /**
     * Maps weather type to corresponding background drawable resource
     */
    fun getBackgroundResource(): Int {
        return when (this) {
            SUNNY -> R.drawable.sunny
            RAINY -> R.drawable.rainy
            CLOUDY -> R.drawable.cloudy
        }
    }

    companion object {
        /**
         * Maps OpenWeatherMap condition codes to WeatherType
         * @param condition Main weather condition from API (e.g., "Clear", "Rain", "Clouds")
         * @return Corresponding WeatherType
         */
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
