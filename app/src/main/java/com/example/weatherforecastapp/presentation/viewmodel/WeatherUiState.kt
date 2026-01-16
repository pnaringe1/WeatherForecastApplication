package com.example.weatherforecastapp.presentation.viewmodel

import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType

/**
 * Sealed interface representing different UI states
 * Follows state management best practices
 */
sealed interface WeatherUiState {
    /**
     * Initial state when loading weather data
     */
    data object Loading : WeatherUiState

    /**
     * Success state with forecast data
     * @param forecasts List of 5-day weather forecasts
     * @param currentWeatherType The predominant weather type for background
     */
    data class Success(
        val forecasts: List<WeatherForecast>,
        val currentWeatherType: WeatherType
    ) : WeatherUiState

    /**
     * Error state with error message
     * @param message Error message to display
     */
    data class Error(val message: String?) : WeatherUiState

    /**
     * State when location permission is not granted
     */
    data object PermissionRequired : WeatherUiState
}
