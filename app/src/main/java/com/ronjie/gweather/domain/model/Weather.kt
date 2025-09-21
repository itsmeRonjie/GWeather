package com.ronjie.gweather.domain.model

import com.ronjie.gweather.data.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Weather(
    val location: String,
    val coordinates: Coordinates,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherId: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val windDegrees: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val visibility: Int,
    val countryCode: String,
    val timestamp: Long
) {

    val formattedTimestamp: String
        get() = formatTimestamp(timestamp)

    fun getSunriseTime(): String {
        return formatTime(sunrise, timezone)
    }

    fun getSunsetTime(): String {
        return formatTime(sunset, timezone)
    }

    fun getWeatherIconUrl(): String {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val isNight = hour >= 18 || hour < 6

        val baseIcon = if (weatherIcon.isNotEmpty()) weatherIcon.dropLast(1) else "01"
        val timeSuffix = if (isNight) "n" else "d"

        return "${Constants.BASE_URL}/img/wn/${baseIcon}${timeSuffix}@2x.png"
    }

    fun isNightTime(currentTime: Long = System.currentTimeMillis() / 1000): Boolean {
        return currentTime < sunrise || currentTime > sunset
    }

    private fun formatTime(timestamp: Long, timezone: Int): String {
        val date = Date((timestamp + timezone) * 1000L)
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    }


    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat(
            "MMM d, yyyy — h:mm a",
            Locale.getDefault()
        ).format(timestamp)
    }
}
