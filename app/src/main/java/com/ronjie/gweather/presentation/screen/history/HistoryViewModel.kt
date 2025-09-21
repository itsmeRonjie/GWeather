package com.ronjie.gweather.presentation.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.data.mapper.toDomain
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadWeatherHistory()
    }

    private fun loadWeatherHistory() {
        viewModelScope.launch {
            try {
                weatherRepository.getWeatherHistory().collectLatest { weatherList ->
                    _uiState.value = if (weatherList.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Success(weatherList.map { it.toDomain() })
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    data class Success(val weatherList: List<Weather>) : HistoryUiState
}
