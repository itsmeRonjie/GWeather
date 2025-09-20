package com.ronjie.gweather.presentation.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.domain.usecase.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        loadInitialWeather()
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val lastLocation = locationRepository.getLastSavedLocationOnce()
            if (lastLocation != null) {
                loadWeather(
                    lastLocation.latitude,
                    lastLocation.longitude,
                    false
                )
            } else {
                _uiState.value =
                    WeatherUiState.Error("No location saved. Please set your location.")
                _errorFlow.tryEmit("No location saved. Please set your location.")
            }
        }
    }

    fun loadWeather(
        latitude: Double,
        longitude: Double,
        saveCoordinates: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            if (saveCoordinates) {
                locationRepository.saveLastLocation(latitude, longitude)
            }

            getCurrentWeather(latitude, longitude)
                .onSuccess { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Unknown error occurred"
                    _uiState.value = WeatherUiState.Error(errorMessage)
                    _errorFlow.tryEmit(errorMessage)
                }
        }
    }
}
