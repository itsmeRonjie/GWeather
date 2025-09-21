package com.ronjie.gweather.navigation

sealed class Screen(val route: String, val title: String) {
    object Auth : Screen("auth", "Auth")
    object Weather : Screen("weather", "Home")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")

    companion object {
        const val AUTH_ROUTE = "auth"
        const val WEATHER_ROUTE = "weather"
        const val HISTORY_ROUTE = "history"
        const val SETTINGS_ROUTE = "settings"

        val bottomNavItems = listOf(
            Weather,
            History
        )
    }
}
