package com.example.weatherforecastapp.data.mapper

import com.example.weatherforecastapp.data.remote.dto.City
import com.example.weatherforecastapp.data.remote.dto.ForecastItem
import com.example.weatherforecastapp.data.remote.dto.Main
import com.example.weatherforecastapp.data.remote.dto.Weather
import com.example.weatherforecastapp.data.remote.dto.WeatherResponse
import com.example.weatherforecastapp.domain.model.WeatherType
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


/**
 * Unit tests for WeatherMapper
 */
class WeatherMapperTest {

    private lateinit var mapper: WeatherMapper

    @Before
    fun setUp() {
        mapper = WeatherMapper()
    }

    @Test
    fun `should map WeatherResponse to domain models correctly`() {
        // Arrange
        val weatherResponse = WeatherResponse(
            list = listOf(
                createForecastItem("2024-01-15 12:00:00", "Clear", 25.0),
                createForecastItem("2024-01-16 12:00:00", "Rain", 20.0),
                createForecastItem("2024-01-17 12:00:00", "Clouds", 22.0),
                createForecastItem("2024-01-18 12:00:00", "Clear", 26.0),
                createForecastItem("2024-01-19 12:00:00", "Rain", 19.0)
            ),
            city = City(name = "Dubai", country = "AE")
        )

        // Act
        val result = mapper.toDomainModel(weatherResponse)

        // Assert
        assertEquals(5, result.size)
        assertEquals(WeatherType.SUNNY, result[0].weatherType)
        assertEquals(WeatherType.RAINY, result[1].weatherType)
        assertEquals(WeatherType.CLOUDY, result[2].weatherType)
        assertEquals(25.0, result[0].temperature)
    }

    @Test
    fun `should select noon forecast when multiple forecasts per day`() {
        // Arrange
        val weatherResponse = WeatherResponse(
            list = listOf(
                createForecastItem("2024-01-15 06:00:00", "Clear", 20.0),
                createForecastItem("2024-01-15 12:00:00", "Clear", 25.0),
                createForecastItem("2024-01-15 18:00:00", "Clear", 23.0),
            ),
            city = City(name = "Dubai", country = "UAE")
        )

        // Act
        val result = mapper.toDomainModel(weatherResponse)

        // Assert
        assertEquals(1, result.size)
        assertEquals(25.0, result[0].temperature) // Should pick noon temperature
    }

    @Test
    fun `should handle different weather conditions correctly`() {
        // Arrange
        val weatherResponse = WeatherResponse(
            list = listOf(
                createForecastItem("2024-01-15 12:00:00", "Clear", 25.0),
                createForecastItem("2024-01-16 12:00:00", "Rain", 20.0),
                createForecastItem("2024-01-17 12:00:00", "Drizzle", 21.0),
                createForecastItem("2024-01-18 12:00:00", "Clouds", 22.0),
                createForecastItem("2024-01-19 12:00:00", "Thunderstorm", 19.0)
            ),
            city = City(name = "Dubai", country = "AE")
        )

        // Act
        val result = mapper.toDomainModel(weatherResponse)

        // Assert
        assertEquals(WeatherType.SUNNY, result[0].weatherType)
        assertEquals(WeatherType.RAINY, result[1].weatherType)
        assertEquals(WeatherType.RAINY, result[2].weatherType)
        assertEquals(WeatherType.CLOUDY, result[3].weatherType)
        assertEquals(WeatherType.RAINY, result[4].weatherType)
    }

    private fun createForecastItem(
        dateTime: String,
        weatherCondition: String,
        temperature: Double
    ): ForecastItem {
        return ForecastItem(
            timestamp = System.currentTimeMillis() / 1000,
            main = Main(
                temperature = temperature,
                feelsLike = temperature,
                tempMin = temperature - 2,
                tempMax = temperature + 2,
                pressure = 1013,
                humidity = 60
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = weatherCondition,
                    description = weatherCondition.lowercase(),
                    icon = "01d"
                )
            ),
            dateTimeText = dateTime
        )
    }
}
