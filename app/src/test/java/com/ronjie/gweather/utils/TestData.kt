package com.ronjie.gweather.utils

import com.ronjie.gweather.data.local.entity.LastLocationEntity
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

object TestData {
    fun createTestWeather(): Weather {
        return Weather(
            location = "Test Location",
            coordinates = Coordinates(40.7128, -74.0060),
            temperature = 25.0,
            weatherDescription = "Clear",
            weatherIcon = "01d",
            humidity = 50,
            pressure = 1012,
            windSpeed = 5.0,
            windDegrees = 180,
            timestamp = System.currentTimeMillis(),
            countryCode = "US",
            feelsLike = 31.0,
            tempMin = 31.0,
            tempMax = 32.0,
            weatherId = 1,
            weatherMain = "Clear",
            cloudiness = 1,
            sunrise = 1758404022,
            sunset = 1758447694,
            timezone = 28800,
            visibility = 12
        )
    }

    fun createTestLocationEntity(): LastLocationEntity {
        return LastLocationEntity(
            id = 1,
            latitude = 40.7128,
            longitude = -74.0060,
            timestamp = System.currentTimeMillis()
        )
    }


    fun createTestWeatherDTO(): WeatherResponseDTO {
        return WeatherResponseDTO(
            name = "Test Location",
            weatherDTO = listOf(WeatherDTO(0, "1", "desc", "10d")),
            mainDTO = MainDTO(25.0, 50.0, 1012.0, 5.0, 180, 0, 0),
            windDTO = WindDTO(5.0, 180),
            coordinatesDTO = CoordinatesDTO(40.7128, -74.0060),
            sysDTO = SysDTO(0, 0, "PH", 1758404022, 1758447694),
            visibility = 12,
            cloudsDTO = CloudsDTO(0),
            dateTime = 1758404022,
            timezone = 28800,
            id = 1,
            code = 200
        )
    }

    fun createTestWeatherEntity(): WeatherEntity {
        return WeatherEntity(
            id = 1,
            location = "Test Location",
            temperature = 25.0,
            weatherDescription = "Clear",
            weatherIcon = "01d",
            humidity = 50,
            pressure = 1012,
            windSpeed = 5.0,
            windDegrees = 180,
            timestamp = System.currentTimeMillis(),
            coordinates = Coordinates(
                latitude = 40.7128,
                longitude = -74.0060
            ),
            countryCode = "US",
            feelsLike = 31.0,
            tempMin = 31.0,
            tempMax = 32.0,
            weatherId = 1,
            weatherMain = "",
            cloudiness = 1,
            sunrise = 1758404022,
            sunset = 1758447694,
            timezone = 28800,
            visibility = 12
        )
    }
}