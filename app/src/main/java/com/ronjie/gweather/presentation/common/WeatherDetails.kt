package com.ronjie.gweather.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ronjie.gweather.domain.model.Weather

@Composable
fun WeatherDetails(weather: Weather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            DetailRow(
                label = "Sunrise",
                value = weather.getSunriseTime()
            )
            CustomDivider()

            DetailRow(
                label = "Sunset",
                value = weather.getSunsetTime()
            )
            CustomDivider()

            DetailRow(
                label = "Feels like",
                value = "${weather.feelsLike.toInt()}°C"
            )
            CustomDivider()

            DetailRow(
                label = "Min/Max",
                value = "${weather.tempMin.toInt()}°C / ${weather.tempMax.toInt()}°C"
            )
            CustomDivider()

            DetailRow(
                label = "Humidity",
                value = "${weather.humidity}%"
            )
            CustomDivider()

            DetailRow(
                label = "Wind",
                value = "${weather.windSpeed.toInt()} m/s"
            )
            CustomDivider()

            DetailRow(
                label = "Pressure",
                value = "${weather.pressure} hPa"
            )
            CustomDivider()

            DetailRow(
                label = "Visibility",
                value = "${weather.visibility / 1000} km"
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
