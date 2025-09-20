package com.ronjie.gweather.domain.model

data class MyLatLong(
    val lat: Double,
    val long: Double
) {
        constructor() : this(
            lat = 0.0,
            long = 0.0
        )
}
