package com.ronjie.gweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ronjie.gweather.data.model.WeatherResponse

@Entity(tableName = "weather_history")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val weatherData: WeatherResponse
)
