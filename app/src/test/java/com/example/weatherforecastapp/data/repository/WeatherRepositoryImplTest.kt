package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.mapper.WeatherMapper
import com.example.weatherforecastapp.data.remote.WeatherApiService
import com.example.weatherforecastapp.data.remote.dto.City
import com.example.weatherforecastapp.data.remote.dto.WeatherResponse
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.mockk.mockk

/**
 * Unit tests for WeatherRepositoryImpl
 */
class WeatherRepositoryImplTest {

    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var apiService: WeatherApiService
    private lateinit var mapper: WeatherMapper

    @Before
    fun setUp() {
        apiService = mockk()
        mapper = mockk()
        repository = WeatherRepositoryImpl(apiService, mapper)
    }

    @Test
    fun when_API_call_succeeds() = runTest {
        // Arrange
        val mockResponse = WeatherResponse(
            list = emptyList(),
            city = City("Dubai", "UAE")
        )
        
        val expectedForecasts = listOf(
            WeatherForecast(
                date = LocalDate.now(),
                dayOfWeek = "Monday",
                temperature = 25.0,
                weatherType = WeatherType.SUNNY
            )
        )

        coEvery { 
            apiService.getForecast(any(), any(), any(), any()) 
        } returns mockResponse
        
        coEvery { mapper.toDomainModel(mockResponse) } returns expectedForecasts

        // Act
        val result = repository.getFiveDayForecast(25.2048, 55.2708)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedForecasts, result.getOrNull())
    }

    @Test
    fun `when API call fails, should return failure`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { 
            apiService.getForecast(any(), any(), any(), any()) 
        } throws exception

        // Act
        val result = repository.getFiveDayForecast(25.2048, 55.2708)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
