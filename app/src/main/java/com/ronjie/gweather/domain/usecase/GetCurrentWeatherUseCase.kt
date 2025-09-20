package com.ronjie.gweather.domain.usecase

import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Weather> {
        return repository.getCurrentWeather(latitude, longitude)
    }
}
