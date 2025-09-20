package com.ronjie.gweather.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSnackbar(
    messageManager: MessageManager,
    modifier: Modifier = Modifier,
    onDismiss: (message: Message) -> Unit = { messageManager.removeMessage(it) },
) {
    val currentMessage = messageManager.messages.firstOrNull()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentMessage) {
        currentMessage?.let { message ->
            snackbarHostState.showSnackbar(message.message)
            delay(3000) // Auto-dismiss after 3 seconds
            onDismiss(message)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp),
            snackbar = { data ->
                Snackbar(
                    containerColor = if (currentMessage?.isError == true) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.inverseSurface
                    },
                    contentColor = if (currentMessage?.isError == true) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.inverseOnSurface
                    },
                    content = {
                        Text(text = data.visuals.message)
                    }
                )
            }
        )
    }
}
