package com.ronjie.gweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ronjie.gweather.domain.model.MyLatLong
import com.ronjie.gweather.presentation.theme.GWeatherTheme
import com.ronjie.gweather.presentation.ui.weather.WeatherScreen
import com.ronjie.gweather.utils.LocationProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationProvider = LocationProvider(this)

        setContent {
            val currentLocation by locationProvider.locationUpdates.collectAsState(
                initial = MyLatLong(0.0, 0.0) // Provide a sensible default
            )

            LaunchedEffect(Unit) {
                if (!locationProvider.hasLocationPermission()) {
                    locationProvider.requestLocationPermissions()
                }
            }

            GWeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = hiltViewModel(),
                        latitude = currentLocation.lat,
                        longitude = currentLocation.long
                    )
                }
            }
        }
    }
}
