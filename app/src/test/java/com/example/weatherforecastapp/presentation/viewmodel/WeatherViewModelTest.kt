package com.example.weatherforecastapp.presentation.viewmodel

import android.location.Location
import app.cash.turbine.test
import com.example.weatherforecastapp.data.location.LocationProvider
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType
import com.example.weatherforecastapp.domain.usecase.GetWeatherForecastUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals


/**
 * Unit tests for WeatherViewModel
 * Demonstrates Test-Driven Development (TDD) practices
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var locationProvider: LocationProvider
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherForecastUseCase = mockk()
        locationProvider = mockk()
        
        // Default mock behavior
        every { locationProvider.hasLocationPermission() } returns true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun when_location_permission_not_granted() = runTest {
        // Arrange
        every { locationProvider.hasLocationPermission() } returns false
        viewModel = WeatherViewModel(getWeatherForecastUseCase, locationProvider)

        // Act & Assert
        viewModel.uiState.test {
            advanceUntilIdle()
            assertTrue(awaitItem() is WeatherUiState.PermissionRequired)
        }
    }

    @Test
    fun `when forecast loaded successfully, should emit Success state`() = runTest {
        // Arrange
        val mockLocation = mockk<Location> {
            every { latitude } returns 25.2048
            every { longitude } returns 55.2708
        }
        
        val testForecasts = listOf(
            WeatherForecast(
                date = LocalDate.now(),
                dayOfWeek = "Monday",
                temperature = 25.0,
                weatherType = WeatherType.SUNNY
            )
        )

        coEvery { locationProvider.getCurrentLocation() } returns Result.success(mockLocation)
        coEvery { 
            getWeatherForecastUseCase(any(), any()) 
        } returns flowOf(Result.success(testForecasts))

        // Act
        viewModel = WeatherViewModel(getWeatherForecastUseCase, locationProvider)

        // Assert
        viewModel.uiState.test {
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is WeatherUiState.Success)
            assertEquals(testForecasts, (state as WeatherUiState.Success).forecasts)
        }
    }

    @Test
    fun `when location fetch fails, should emit Error state`() = runTest {
        // Arrange
        val errorMessage = "Location not available"
        coEvery { 
            locationProvider.getCurrentLocation() 
        } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel = WeatherViewModel(getWeatherForecastUseCase, locationProvider)

        // Assert
        viewModel.uiState.test {
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is WeatherUiState.Error)
            assertTrue((state as WeatherUiState.Error).message?.contains(errorMessage) == true)
        }
    }

    @Test
    fun `when API call fails, should emit Error state`() = runTest {
        // Arrange
        val mockLocation = mockk<Location> {
            every { latitude } returns 25.2048
            every { longitude } returns 55.2708
        }
        
        val errorMessage = "Network error"
        coEvery { locationProvider.getCurrentLocation() } returns Result.success(mockLocation)
        coEvery { 
            getWeatherForecastUseCase(any(), any()) 
        } returns flowOf(Result.failure(Exception(errorMessage)))

        // Act
        viewModel = WeatherViewModel(getWeatherForecastUseCase, locationProvider)

        // Assert
        viewModel.uiState.test {
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is WeatherUiState.Error)
            assertEquals(errorMessage, (state as WeatherUiState.Error).message)
        }
    }

    @Test
    fun `retry should reload weather data`() = runTest {
        // Arrange
        val mockLocation = mockk<Location> {
            every { latitude } returns 25.2048
            every { longitude } returns 55.2708
        }
        
        val testForecasts = listOf(
            WeatherForecast(
                date = LocalDate.now(),
                dayOfWeek = "Monday",
                temperature = 25.0,
                weatherType = WeatherType.SUNNY
            )
        )

        coEvery { locationProvider.getCurrentLocation() } returns Result.success(mockLocation)
        coEvery { 
            getWeatherForecastUseCase(any(), any()) 
        } returns flowOf(Result.success(testForecasts))

        viewModel = WeatherViewModel(getWeatherForecastUseCase, locationProvider)
        advanceUntilIdle()

        // Act
        viewModel.retry()

        // Assert
        advanceUntilIdle()
        coVerify(exactly = 2) { getWeatherForecastUseCase(any(), any()) }
    }
}
