package com.ronjie.gweather.presentation.screen.history

import com.ronjie.gweather.domain.model.Weather

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    data class Success(val weatherList: List<Weather>) : HistoryUiState
}
