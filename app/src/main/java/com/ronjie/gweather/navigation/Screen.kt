package com.ronjie.gweather.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Weather : Screen("weather")
    
    companion object {
        const val AUTH_ROUTE = "auth"
        const val WEATHER_ROUTE = "weather"
    }
}
