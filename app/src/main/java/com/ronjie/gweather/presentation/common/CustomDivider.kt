package com.ronjie.gweather.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = DividerDefaults.Thickness,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}
