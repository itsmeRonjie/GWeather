package com.ronjie.gweather.domain.repository

import com.ronjie.gweather.data.local.entity.WeatherEntity
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather>
    fun getCachedWeather(): Flow<Weather?>

    suspend fun saveWeather(weather: Weather, locationName: String, lat: Double, lon: Double)
    fun getWeatherHistory(): Flow<List<WeatherEntity>>
    fun searchWeatherHistory(query: String): Flow<List<WeatherEntity>>

    suspend fun getLastSavedLocationOnce(): Coordinates?
}
