package com.example.weatherforecastapp.presentation.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType
import com.example.weatherforecastapp.presentation.viewmodel.WeatherUiState
import com.example.weatherforecastapp.presentation.viewmodel.WeatherViewModel
import com.example.weatherforecastapp.presentation.ui.components.ForecastCard
import com.example.weatherforecastapp.presentation.ui.components.LoadingScreen


/**
 * Main weather forecast screen
 */
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var permissionRequested by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.onPermissionGranted()
        } else {
            permissionRequested = true
        }
    }

    // Request permission on initial load if needed
    LaunchedEffect(uiState) {
        if (uiState is WeatherUiState.PermissionRequired) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    when (val state = uiState) {
        is WeatherUiState.Loading -> LoadingScreen()
        
        is WeatherUiState.Success -> WeatherContent(
            forecasts = state.forecasts,
            weatherType = state.currentWeatherType
        )
        
        is WeatherUiState.Error -> ErrorScreen(
            message = state.message ?: "Unknown error",
            onRetry = { viewModel.retry() }
        )
        
        is WeatherUiState.PermissionRequired -> PermissionRequiredScreen(
            onRequestPermission = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

/**
 * Displays weather forecast content with background image
 */
@Composable
private fun WeatherContent(
    forecasts: List<WeatherForecast>,
    weatherType: WeatherType
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = weatherType.getBackgroundResource()),
            contentDescription = "Weather background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Forecast list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Text(
                    text = "5 Day Forecast",
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(28.dp)

                )
                HorizontalDivider(
                    thickness = 1.dp, // Set the thickness of the line
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            items(forecasts) { forecast ->
                ForecastCard(forecast = forecast)
            }
        }
    }
}

/**
 * Error screen with retry button
 */
@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Permission required screen
 */
@Composable
private fun PermissionRequiredScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "This app needs location access to show weather forecast for your area.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "If you previously denied permission, please:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "1. Go to Settings\n2. Select Apps\n3. Select Weather App\n4. Enable Location permission",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}
