package com.ronjie.gweather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponseDTO(
    @SerializedName("coord")
    val coordinatesDTO: CoordinatesDTO?,
    @SerializedName("weather")
    val weatherDTO: List<WeatherDTO>?,
    @SerializedName("main")
    val mainDTO: MainDTO?,
    @SerializedName("visibility")
    val visibility: Int?,
    @SerializedName("wind")
    val windDTO: WindDTO?,
    @SerializedName("clouds")
    val cloudsDTO: CloudsDTO?,
    @SerializedName("dt")
    val dateTime: Long?,
    @SerializedName("sys")
    val sysDTO: SysDTO?,
    @SerializedName("timezone")
    val timezone: Int?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("cod")
    val code: Int?
)

data class CoordinatesDTO(
    @SerializedName("lon")
    val longitude: Double?,
    @SerializedName("lat")
    val latitude: Double?
)

data class WeatherDTO(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("main")
    val main: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("icon")
    val icon: String?
)

data class MainDTO(
    @SerializedName("temp")
    val temperature: Double?,
    @SerializedName("feels_like")
    val feelsLike: Double?,
    @SerializedName("temp_min")
    val tempMin: Double?,
    @SerializedName("temp_max")
    val tempMax: Double?,
    @SerializedName("pressure")
    val pressure: Int?,
    @SerializedName("humidity")
    val humidity: Int?,
    @SerializedName("sea_level")
    val seaLevel: Int? = null,
    @SerializedName("grnd_level")
    val groundLevel: Int? = null
)

data class WindDTO(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val degrees: Int,
    @SerializedName("gust")
    val gust: Double? = null
)

data class CloudsDTO(
    @SerializedName("all")
    val all: Int?
)

data class SysDTO(
    @SerializedName("type")
    val type: Int?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("sunrise")
    val sunrise: Long?,
    @SerializedName("sunset")
    val sunset: Long?
)
