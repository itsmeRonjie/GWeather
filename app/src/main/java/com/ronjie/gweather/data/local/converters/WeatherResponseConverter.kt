package com.ronjie.gweather.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ronjie.gweather.data.model.WeatherResponse

class WeatherResponseConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String): WeatherResponse {
        val type = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(weatherResponseString, type)
    }
}
