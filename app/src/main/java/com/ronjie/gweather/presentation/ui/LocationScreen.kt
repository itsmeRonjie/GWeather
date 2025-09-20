package com.ronjie.gweather.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ronjie.gweather.domain.model.MyLatLong

@Composable
fun LocationScreen(
    modifier: Modifier = Modifier,
    currentLocation: MyLatLong,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (hasPermission) {
            Text(
                text = "Current location: ${currentLocation.lat}, ${currentLocation.long}"
            )
        } else {
            Text(
                text = "Location permission is needed to show your current location."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}