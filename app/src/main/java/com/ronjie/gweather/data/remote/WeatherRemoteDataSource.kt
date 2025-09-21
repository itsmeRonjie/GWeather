package com.ronjie.gweather.data.remote

import com.ronjie.gweather.data.model.WeatherResponseDTO
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherResponseDTO {
        return weatherApi.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            apiKey = apiKey
        )
    }
}
