package com.example.weatherforecastapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherforecastapp.data.remote.dto.WeatherResponse
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs

/**
 * Mapper class to convert API DTOs to domain models
 * Follows Single Responsibility Principle
 */
class WeatherMapper @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Converts WeatherResponse DTO to list of WeatherForecast domain models
     * Groups forecasts by day and takes the forecast closest to noon for each day
     * 
     * @param response API response
     * @return List of 5 WeatherForecast objects
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomainModel(response: WeatherResponse): List<WeatherForecast> {
        // Group forecasts by date
        val forecastsByDate = response.list
            .groupBy { item ->
                val dateTime = LocalDateTime.parse(item.dateTimeText, dateTimeFormatter)
                dateTime.toLocalDate()
            }
            .mapValues { (_, items) ->
                // For each day, pick the forecast closest to 12:00 (noon)
                items.minByOrNull { item ->
                    val dateTime = LocalDateTime.parse(item.dateTimeText, dateTimeFormatter)
                    abs(dateTime.hour - 12)
                }
            }
            .filterValues { it != null }
            .mapValues { it.value!! }

        // Take the next 5 days
        return forecastsByDate.entries
            .sortedBy { it.key }
            .take(5)
            .map { (date, item) ->
                WeatherForecast(
                    date = date,
                    dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    temperature = item.main.temperature,
                    weatherType = WeatherType.fromCondition(item.weather.firstOrNull()?.main ?: "")
                )
            }
    }
}
