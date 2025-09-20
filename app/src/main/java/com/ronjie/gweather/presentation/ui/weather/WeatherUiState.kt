package com.ronjie.gweather.presentation.ui.weather

import com.ronjie.gweather.domain.model.Weather

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: Weather) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
