package com.ronjie.gweather.domain.repository

import com.ronjie.gweather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather>
    fun getCachedWeather(): Flow<Weather?>
}
