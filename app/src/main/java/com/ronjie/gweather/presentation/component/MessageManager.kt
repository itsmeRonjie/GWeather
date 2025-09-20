package com.ronjie.gweather.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
class MessageManager {
    private val _currentMessage = MutableStateFlow<Message?>(null)
    val currentMessage: StateFlow<Message?> = _currentMessage.asStateFlow()

    fun showMessage(message: String, isError: Boolean = false) {
        _currentMessage.update { Message(message = message, isError = isError) }
    }

    fun dismissMessage() {
        _currentMessage.update { null }
    }
}

@Composable
fun rememberMessageManager(): MessageManager {
    return remember { MessageManager() }
}
