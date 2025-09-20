package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.local.entity.WeatherEntity
import com.ronjie.gweather.data.mapper.toDomain
import com.ronjie.gweather.data.mapper.toEntity
import com.ronjie.gweather.data.remote.WeatherRemoteDataSource
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val apiKey: String
) : WeatherRepository {

    private var cachedWeather: Weather? = null

    private fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 &&
                longitude in -180.0..180.0 &&
                (latitude != 0.0 || longitude != 0.0)
    }

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<Weather> {
        return try {
            val response = remoteDataSource.getCurrentWeather(latitude, longitude, apiKey)

            val weather =
                if (response.coordinates?.latitude == 0.0 && response.coordinates.longitude == 0.0) {
                    response.copy(
                        coordinates = response.coordinates.copy(
                            latitude = latitude,
                            longitude = longitude
                        )
                    ).toDomain()
                } else {
                    response.toDomain()
                }

            if (areValidCoordinates(weather.coordinates.latitude, weather.coordinates.longitude)) {
                cachedWeather = weather
            }

            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedWeather(): Flow<Weather?> = flow {
        emit(cachedWeather)
    }

    override suspend fun saveWeather(
        weather: Weather,
        locationName: String,
        lat: Double,
        lon: Double
    ) {
        if (areValidCoordinates(lat, lon)) {
            localDataSource.saveWeather(weather.toEntity(), locationName, lat, lon)
        }
    }

    override fun getWeatherHistory(): Flow<List<WeatherEntity>> {
        return localDataSource.getWeatherHistory()
    }

    override fun searchWeatherHistory(query: String): Flow<List<WeatherEntity>> {
        return localDataSource.searchWeatherHistory(query)
    }


    override suspend fun getLastSavedLocationOnce(): Coordinates? {
        val latestWeather = localDataSource.getWeatherHistory()
            .firstOrNull()
            ?.maxByOrNull { it.timestamp }

        return latestWeather?.let {
            Coordinates(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }
}
