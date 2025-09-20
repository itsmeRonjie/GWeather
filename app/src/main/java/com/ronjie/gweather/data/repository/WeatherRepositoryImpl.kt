package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.mapper.toDomain
import com.ronjie.gweather.data.remote.WeatherRemoteDataSource
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val apiKey: String
) : WeatherRepository {

    private var cachedWeather: Weather? = null

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<Weather> {

        return try {
            val response = remoteDataSource.getCurrentWeather(latitude, longitude, apiKey)
            val weather = response.toDomain()
            cachedWeather = weather
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedWeather(): Flow<Weather?> = flow {
        emit(cachedWeather)
    }
}
