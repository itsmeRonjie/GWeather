package com.ronjie.gweather.data.mapper

import com.ronjie.gweather.data.model.WeatherResponse
import com.ronjie.gweather.domain.model.Weather

fun WeatherResponse.toDomain(): Weather {
    val weather = weather.firstOrNull()
    return Weather(
        location = name,
        coordinates = com.ronjie.gweather.domain.model.Coordinates(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude
        ),
        temperature = main.temperature,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        pressure = main.pressure,
        humidity = main.humidity,
        weatherDescription = weather?.description ?: "",
        weatherIcon = weather?.icon ?: "",
        windSpeed = wind.speed,
        windDegrees = wind.degrees,
        cloudiness = clouds.all,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        timezone = timezone,
        visibility = visibility
    )
}
