package com.ronjie.gweather.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
class MessageManager {
    private val _messages = mutableStateListOf<Message>()
    val messages: SnapshotStateList<Message> = _messages

    fun showMessage(message: String, isError: Boolean = false) {
        _messages.add(Message(message = message, isError = isError))
    }

    fun removeMessage(message: Message) {
        _messages.remove(message)
    }
}

@Composable
fun rememberMessageManager(): MessageManager {
    return remember { MessageManager() }
}
