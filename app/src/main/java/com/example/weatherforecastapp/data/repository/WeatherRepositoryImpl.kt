package com.example.weatherforecastapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherforecastapp.data.mapper.WeatherMapper
import com.example.weatherforecastapp.data.remote.WeatherApiService
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Implementation of WeatherRepository
 * Handles data fetching and error handling
 */
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val mapper: WeatherMapper
) : WeatherRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<WeatherForecast>> {
        return try {
            val response = apiService.getForecast(
                latitude = latitude,
                longitude = longitude,
                apiKey = "aab13232e409ade1e0d4e7227f900814"
            )
            
            val forecasts = mapper.toDomainModel(response)
            Result.success(forecasts)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
