package com.ronjie.gweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ronjie.gweather.domain.model.Coordinates

@Entity(tableName = "weather_history")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val location: String,
    val coordinates: Coordinates,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherId: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val windDegrees: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val visibility: Int,
    val countryCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
