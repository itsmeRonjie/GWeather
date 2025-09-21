package com.ronjie.gweather.presentation.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.domain.repository.WeatherRepository
import com.ronjie.gweather.domain.usecase.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var currentWeather: Weather? = null
        set(value) {
            field = value
            value?.let { _uiState.value = WeatherUiState.Success(it) }
        }

    init {
        loadInitialWeather()
        observeCachedWeather()
    }

    private fun observeCachedWeather() {
        viewModelScope.launch {
            weatherRepository.getCachedWeather().collectLatest { cachedWeather ->
                cachedWeather?.let {
                    currentWeather = it
                }
            }
        }
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val lastLocation = locationRepository.getLastSavedLocationOnce()
            if (lastLocation != null) {
                val cachedWeather = weatherRepository.getCachedWeather().firstOrNull()
                if (cachedWeather != null) {
                    currentWeather = cachedWeather
                }
                loadWeather(
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude,
                    saveCoordinates = true,
                    saveToDatabase = true
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
        saveCoordinates: Boolean = true,
        forceRefresh: Boolean = false,
        saveToDatabase: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                if (saveCoordinates) {
                    locationRepository.saveLastLocation(latitude, longitude)
                }

                if (currentWeather == null || forceRefresh) {
                    _uiState.value = WeatherUiState.Loading
                }

                getCurrentWeather(latitude, longitude, saveToDatabase)
                    .onSuccess { weather ->
                        currentWeather = weather
                    }
                    .onFailure { exception ->
                        if (currentWeather == null) {
                            val errorMessage = exception.message ?: "Unknown error occurred"
                            _uiState.value = WeatherUiState.Error(errorMessage)
                            _errorFlow.tryEmit("Failed to update weather: $errorMessage")
                        } else {
                            _errorFlow.tryEmit("Using cached data: ${exception.message}")
                        }
                    }
            } catch (e: Exception) {
                if (currentWeather == null) {
                    _uiState.value = WeatherUiState.Error("Failed to load weather")
                    _errorFlow.tryEmit("Failed to load weather: ${e.message}")
                }
            }
        }
    }
}
