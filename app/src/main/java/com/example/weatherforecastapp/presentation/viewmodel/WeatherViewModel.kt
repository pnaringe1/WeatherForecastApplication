package com.example.weatherforecastapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.domain.model.WeatherType
import com.example.weatherforecastapp.data.location.LocationProvider
import com.example.weatherforecastapp.domain.usecase.GetWeatherForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for weather forecast screen
 * Manages UI state and coordinates data fetching
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val mUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = mUiState.asStateFlow()

    init {
        loadWeather()
    }

    /**
     * Loads weather data based on current location
     */
    fun loadWeather() {
        viewModelScope.launch {
            mUiState.value = WeatherUiState.Loading

            // Check if location permission is granted
            if (!locationProvider.hasLocationPermission()) {
                mUiState.value = WeatherUiState.PermissionRequired
                return@launch
            }

            // Get current location
            locationProvider.getCurrentLocation()
                .onSuccess { location ->
                    // Fetch weather forecast
                    getWeatherForecastUseCase(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ).collect { result ->
                        mUiState.value = when {
                            result.isSuccess -> {
                                val forecasts = result.getOrNull()!!
                                // Use the first day's weather type for background
                                val currentWeatherType = forecasts.firstOrNull()?.weatherType 
                                    ?: WeatherType.SUNNY
                                
                                WeatherUiState.Success(
                                    forecasts = forecasts,
                                    currentWeatherType = currentWeatherType
                                )
                            }
                            else -> WeatherUiState.Error(
                                result.exceptionOrNull()?.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    mUiState.value = WeatherUiState.Error(
                        exception.message ?: "Failed to get location"
                    )
                }
        }
    }

    /**
     * Called when user grants location permission
     */
    fun onPermissionGranted() {
        loadWeather()
    }

    /**
     * Retry loading weather data
     */
    fun retry() {
        loadWeather()
    }
}
