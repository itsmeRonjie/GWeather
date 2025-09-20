package com.ronjie.gweather.data.mapper

import com.ronjie.gweather.data.model.Clouds
import com.ronjie.gweather.data.model.Coordinates
import com.ronjie.gweather.data.model.Main
import com.ronjie.gweather.data.model.Sys
import com.ronjie.gweather.data.model.WeatherResponse
import com.ronjie.gweather.data.model.Wind
import com.ronjie.gweather.data.model.Weather as DataWeather
import com.ronjie.gweather.domain.model.Weather as DomainWeather

fun DomainWeather.toEntity(): WeatherResponse {
    return WeatherResponse(
        coordinates = Coordinates(
            latitude = this.coordinates.latitude,
            longitude = this.coordinates.longitude
        ),
        weather = listOf(
            DataWeather(
                id = this.weatherId,
                main = this.weatherMain,
                description = this.weatherDescription,
                icon = this.weatherIcon
            )
        ),
        main = Main(
            temperature = this.temperature,
            feelsLike = this.feelsLike,
            tempMin = this.tempMin,
            tempMax = this.tempMax,
            pressure = this.pressure,
            humidity = this.humidity
        ),
        visibility = this.visibility,
        wind = Wind(
            speed = this.windSpeed,
            degrees = this.windDegrees
        ),
        clouds = Clouds(
            all = this.cloudiness
        ),
        dateTime = this.timestamp,
        sys = Sys(
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
