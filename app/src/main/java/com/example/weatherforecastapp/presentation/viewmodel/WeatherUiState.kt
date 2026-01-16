package com.example.weatherforecastapp.presentation.viewmodel

import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType

/**
 * Sealed interface representing different UI states
 */
sealed interface WeatherUiState {
    /**
     * Initial state when loading weather data
     */
    data object Loading : WeatherUiState

    data class Success(
        val forecasts: List<WeatherForecast>,
        val currentWeatherType: WeatherType
    ) : WeatherUiState

    data class Error(val message: String?) : WeatherUiState

    data object PermissionRequired : WeatherUiState
}
