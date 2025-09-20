package com.ronjie.gweather.presentation.screen.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ronjie.gweather.domain.model.Weather
import com.ronjie.gweather.presentation.common.WeatherDetailCard
import com.ronjie.gweather.utils.getWindDirection
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeatherScreen(
    modifier: Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
    latitude: Double,
    longitude: Double,
    onError: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentWeather by remember { mutableStateOf<Weather?>(null) }

    LaunchedEffect(latitude, longitude) {
        viewModel.loadWeather(latitude, longitude)
    }

    LaunchedEffect(uiState) {
        if (uiState is WeatherUiState.Success) {
            currentWeather = (uiState as WeatherUiState.Success).weather
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { error ->
            onError(error)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            currentWeather?.let { weather ->
                WeatherContent(weather = weather)
            }

            if (uiState is WeatherUiState.Loading && currentWeather == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (uiState is WeatherUiState.Loading && currentWeather != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
            }

            if (uiState is WeatherUiState.Error && currentWeather == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as WeatherUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.location,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "${weather.temperature.toInt()}°C",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = weather.weatherDescription.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AsyncImage(
            model = weather.getWeatherIconUrl(),
            contentDescription = weather.weatherDescription,
            modifier = Modifier.size(120.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                WeatherDetailCard(
                    title = "Sunrise",
                    value = weather.getSunriseTime()
                )
                WeatherDetailCard(
                    title = "Wind",
                    value = "${weather.windSpeed} m/s",
                    additionalInfo = getWindDirection(weather.windDegrees)
                )
                WeatherDetailCard(
                    title = "Humidity",
                    value = "${weather.humidity}%"
                )
                WeatherDetailCard(
                    title = "Feels like",
                    value = "${weather.feelsLike.toInt()}°C"
                )
                WeatherDetailCard(
                    title = "Sunset",
                    value = weather.getSunsetTime()
                )
                WeatherDetailCard(
                    title = "Pressure",
                    value = "${weather.pressure} hPa"
                )
                WeatherDetailCard(
                    title = "Visibility",
                    value = "${weather.visibility / 1000} km"
                )
            }
        }
    }
}
