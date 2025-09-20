package com.ronjie.gweather.utils

fun getWindDirection(degrees: Int): String? {
    if (degrees < 0 || degrees > 360) return null

    return when {
        degrees >= 348.75 || degrees < 11.25 -> "N"
        degrees < 33.75 -> "NNE"
        degrees < 56.25 -> "NE"
        degrees < 78.75 -> "ENE"
        degrees < 101.25 -> "E"
        degrees < 123.75 -> "ESE"
        degrees < 146.25 -> "SE"
        degrees < 168.75 -> "SSE"
        degrees < 191.25 -> "S"
        degrees < 213.75 -> "SSW"
        degrees < 236.25 -> "SW"
        degrees < 258.75 -> "WSW"
        degrees < 281.25 -> "W"
        degrees < 303.75 -> "WNW"
        degrees < 326.25 -> "NW"
        else -> "NNW"
    }.let { "$degrees° $it" }
}
