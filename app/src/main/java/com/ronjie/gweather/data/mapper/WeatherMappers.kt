package com.ronjie.gweather.data.mapper

import com.ronjie.gweather.data.local.entity.WeatherEntity
import com.ronjie.gweather.data.model.CloudsDTO
import com.ronjie.gweather.data.model.CoordinatesDTO
import com.ronjie.gweather.data.model.MainDTO
import com.ronjie.gweather.data.model.SysDTO
import com.ronjie.gweather.data.model.WeatherDTO
import com.ronjie.gweather.data.model.WeatherResponseDTO
import com.ronjie.gweather.data.model.WindDTO
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.model.Weather

fun Weather.toDTO(): WeatherResponseDTO {
    return WeatherResponseDTO(
        coordinatesDTO = CoordinatesDTO(
            latitude = this.coordinates.latitude,
            longitude = this.coordinates.longitude
        ),
        weatherDTO = listOf(
            WeatherDTO(
                id = this.weatherId,
                main = this.weatherMain,
                description = this.weatherDescription,
                icon = this.weatherIcon
            )
        ),
        mainDTO = MainDTO(
            temperature = this.temperature,
            feelsLike = this.feelsLike,
            tempMin = this.tempMin,
            tempMax = this.tempMax,
            pressure = this.pressure,
            humidity = this.humidity
        ),
        visibility = this.visibility,
        windDTO = WindDTO(
            speed = this.windSpeed,
            degrees = this.windDegrees
        ),
        cloudsDTO = CloudsDTO(
            all = this.cloudiness
        ),
        dateTime = this.timestamp,
        sysDTO = SysDTO(
            type = 0,
            id = 0,
            country = "",
            sunrise = this.sunrise,
            sunset = this.sunset
        ),
        timezone = 0,
        id = 0,
        name = this.location,
        code = 200
    )
}

fun WeatherResponseDTO.toDomain(): Weather {
    val weatherInfo = weatherDTO?.firstOrNull()
    return Weather(
        location = name.orEmpty(),
        coordinates = Coordinates(
            latitude = coordinatesDTO?.latitude ?: 0.0,
            longitude = coordinatesDTO?.longitude ?: 0.0
        ),
        temperature = mainDTO?.temperature ?: 0.0,
        feelsLike = mainDTO?.feelsLike ?: 0.0,
        tempMin = mainDTO?.tempMin ?: 0.0,
        tempMax = mainDTO?.tempMax ?: 0.0,
        pressure = mainDTO?.pressure ?: 0,
        humidity = mainDTO?.humidity ?: 0,
        weatherId = weatherInfo?.id ?: 0,
        weatherMain = weatherInfo?.main ?: "",
        weatherDescription = weatherInfo?.description ?: "",
        weatherIcon = weatherInfo?.icon ?: "01d",
        windSpeed = windDTO?.speed ?: 0.0,
        windDegrees = windDTO?.degrees ?: 0,
        cloudiness = cloudsDTO?.all ?: 0,
        sunrise = sysDTO?.sunrise ?: 0L,
        sunset = sysDTO?.sunset ?: 0L,
        timezone = timezone ?: 0,
        visibility = visibility ?: 0,
        countryCode = sysDTO?.country ?: "PH",
        timestamp = dateTime ?: 0L
    )
}

fun WeatherResponseDTO.toEntity(): WeatherEntity {
    val weatherInfo = weatherDTO?.firstOrNull()
    return WeatherEntity(
        location = name.orEmpty(),
        coordinates = Coordinates(
            latitude = coordinatesDTO?.latitude ?: 0.0,
            longitude = coordinatesDTO?.longitude ?: 0.0
        ),
        temperature = mainDTO?.temperature ?: 0.0,
        feelsLike = mainDTO?.feelsLike ?: 0.0,
        tempMin = mainDTO?.tempMin ?: 0.0,
        tempMax = mainDTO?.tempMax ?: 0.0,
        pressure = mainDTO?.pressure ?: 0,
        humidity = mainDTO?.humidity ?: 0,
        weatherId = weatherInfo?.id ?: 0,
        weatherMain = weatherInfo?.main ?: "",
        weatherDescription = weatherInfo?.description ?: "",
        weatherIcon = weatherInfo?.icon ?: "01d",
        windSpeed = windDTO?.speed ?: 0.0,
        windDegrees = windDTO?.degrees ?: 0,
        cloudiness = cloudsDTO?.all ?: 0,
        sunrise = sysDTO?.sunrise ?: 0L,
        sunset = sysDTO?.sunset ?: 0L,
        timezone = timezone ?: 0,
        visibility = visibility ?: 0,
        countryCode = sysDTO?.country ?: "PH"
    )
}
