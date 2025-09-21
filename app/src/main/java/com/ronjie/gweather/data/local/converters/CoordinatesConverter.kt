package com.ronjie.gweather.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ronjie.gweather.domain.model.Coordinates

class CoordinatesConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherCoordinates(coordinates: Coordinates): String {
        return gson.toJson(coordinates)
    }

    @TypeConverter
    fun toWeatherCoordinates(coordinatesString: String): Coordinates {
        val type = object : TypeToken<Coordinates>() {}.type
        return gson.fromJson(coordinatesString, type)
    }
}
