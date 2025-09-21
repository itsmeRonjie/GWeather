package com.ronjie.gweather.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.utils.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    locationProvider: LocationProvider,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _currentLocation = MutableStateFlow(Coordinates(0.0, 0.0))
    val currentLocation: StateFlow<Coordinates> = _currentLocation.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                locationRepository.getLastSavedLocationOnce()?.let { savedLocation ->
                    _currentLocation.value = Coordinates(
                        latitude = savedLocation.latitude,
                        longitude = savedLocation.longitude
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading last saved location", e)
            }
        }

        locationProvider.locationUpdates
            .onEach { coordinates ->
                _currentLocation.value = coordinates

                try {
                    locationRepository.saveLastLocation(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude
                    )
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error saving location to database", e)
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateLocation(coordinates: Coordinates) {
        _currentLocation.value = coordinates
        viewModelScope.launch {
            try {
                locationRepository.saveLastLocation(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error saving location to database", e)
            }
        }
    }
}

