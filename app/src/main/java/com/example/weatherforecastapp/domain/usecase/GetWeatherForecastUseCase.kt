package com.example.weatherforecastapp.domain.usecase

import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for fetching weather forecast
 * Encapsulates business logic and follows Single Responsibility Principle
 */
class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    /**
     * Executes the use case to get 5-day weather forecast
     * @param latitude User's current latitude
     * @param longitude User's current longitude
     * @return Flow emitting Result with list of WeatherForecast
     */
    operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Flow<Result<List<WeatherForecast>>> = flow {
        emit(weatherRepository.getFiveDayForecast(latitude, longitude))
    }
}
