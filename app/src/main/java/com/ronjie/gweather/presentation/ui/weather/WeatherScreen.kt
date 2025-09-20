package com.ronjie.gweather.presentation.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.presentation.ui.weather.WeatherUiState.Error
import com.ronjie.gweather.presentation.ui.weather.WeatherUiState.Loading
import com.ronjie.gweather.presentation.ui.weather.WeatherUiState.Success

@Composable
fun WeatherScreen(
    modifier: Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
    latitude: Double,
    longitude: Double,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = latitude, key2 = longitude) {
        viewModel.loadWeather(latitude, longitude)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is Success -> {
                WeatherContent(weather = state.weather)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherContent(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location
        Text(
            text = weather.location,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Current temperature
        Text(
            text = "${weather.temperature.toInt()}°C",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        // Weather description
        Text(
            text = weather.weatherDescription.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Weather details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                WeatherDetailItem("Feels like", "${weather.feelsLike.toInt()}°C")
                WeatherDetailItem(
                    "Min/Max",
                    "${weather.tempMin.toInt()}°C / ${weather.tempMax.toInt()}°C"
                )
                WeatherDetailItem("Humidity", "${weather.humidity}%")
                WeatherDetailItem("Wind", "${weather.windSpeed} m/s")
                WeatherDetailItem("Pressure", "${weather.pressure} hPa")
                WeatherDetailItem("Visibility", "${weather.visibility / 1000} km")
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
