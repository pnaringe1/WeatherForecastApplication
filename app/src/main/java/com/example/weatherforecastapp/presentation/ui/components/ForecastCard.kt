package com.example.weatherforecastapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforecastapp.domain.model.WeatherForecast
import com.example.weatherforecastapp.domain.model.WeatherType

/**
 * Card displaying a single day's weather forecast
 */
@Composable
fun ForecastCard(
    forecast: WeatherForecast,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column()
            {
            // Day name
            Text(
                text = forecast.dayOfWeek,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,

                )

            // Weather icon placeholder
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = getWeatherEmoji(forecast.weatherType),
                fontSize = 32.sp
            )
        }

        // Temperature
        Text(
            text = forecast.getFormattedTemperature(),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

/**
 * Returns emoji representation of weather type
 * You can replace this with actual weather icons from assets
 */
private fun getWeatherEmoji(weatherType: WeatherType): String {
    return when (weatherType) {
        WeatherType.SUNNY -> "☀️"
        WeatherType.RAINY -> "🌧️"
        WeatherType.CLOUDY -> "☁️"
    }
}
