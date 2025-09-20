package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.local.dao.WeatherDao
import com.ronjie.gweather.data.local.entity.WeatherEntity
import com.ronjie.gweather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WeatherLocalDataSource {
    suspend fun saveWeather(
        weather: WeatherResponse,
        locationName: String,
        lat: Double,
        lon: Double
    )

    fun getWeatherHistory(): Flow<List<WeatherEntity>>
    suspend fun getWeatherById(id: Long): WeatherEntity?
    fun searchWeatherHistory(query: String): Flow<List<WeatherEntity>>
}

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao
) : WeatherLocalDataSource {

    override suspend fun saveWeather(
        weather: WeatherResponse,
        locationName: String,
        lat: Double,
        lon: Double
    ) {
        val entity = WeatherEntity(
            locationName = locationName,
            latitude = lat,
            longitude = lon,
            weatherData = weather
        )
        weatherDao.insertWeather(entity)
    }

    override fun getWeatherHistory(): Flow<List<WeatherEntity>> {
        return weatherDao.getAllWeatherHistory()
    }

    override suspend fun getWeatherById(id: Long): WeatherEntity? {
        return weatherDao.getWeatherById(id)
    }

    override fun searchWeatherHistory(query: String): Flow<List<WeatherEntity>> {
        return weatherDao.searchWeatherHistory(query)
    }
}
