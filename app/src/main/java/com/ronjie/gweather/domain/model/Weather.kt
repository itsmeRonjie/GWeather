package com.ronjie.gweather.domain.model

data class Weather(
    val location: String,
    val coordinates: Coordinates,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val windDegrees: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val visibility: Int
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
