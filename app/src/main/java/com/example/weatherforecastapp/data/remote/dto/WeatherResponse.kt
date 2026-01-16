package com.example.weatherforecastapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for OpenWeatherMap API responses
 */

data class WeatherResponse(
    @SerializedName("list")
    val list: List<ForecastItem>,
    @SerializedName("city")
    val city: City
)

data class ForecastItem(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("dt_txt")
    val dateTimeText: String
)

data class Main(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int
)

data class Weather(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

data class City(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
)
