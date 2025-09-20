package com.ronjie.gweather.data.mapper

import com.ronjie.gweather.data.model.WeatherResponse
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.model.Weather

fun WeatherResponse.toDomain(): Weather {
    val weatherInfo = weather?.firstOrNull()
    return Weather(
        location = name.orEmpty(),
        coordinates = Coordinates(
            latitude = coordinates?.latitude ?: 0.0,
            longitude = coordinates?.longitude ?: 0.0
        ),
        temperature = main?.temperature ?: 0.0,
        feelsLike = main?.feelsLike ?: 0.0,
        tempMin = main?.tempMin ?: 0.0,
        tempMax = main?.tempMax ?: 0.0,
        pressure = main?.pressure ?: 0,
        humidity = main?.humidity ?: 0,
        weatherId = weatherInfo?.id ?: 0,
        weatherMain = weatherInfo?.main ?: "",
        weatherDescription = weatherInfo?.description ?: "",
        weatherIcon = weatherInfo?.icon ?: "01d",
        windSpeed = wind?.speed ?: 0.0,
        windDegrees = wind?.degrees ?: 0,
        cloudiness = clouds?.all ?: 0,
        sunrise = sys?.sunrise ?: 0L,
        sunset = sys?.sunset ?: 0L,
        timezone = timezone ?: 0,
        visibility = visibility ?: 0,
        countryCode = sys?.country ?: "PH",
        timestamp = dateTime ?: 0L
    )
}
