package com.ronjie.gweather.presentation.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ronjie.gweather.utils.findActivity

@Composable
fun PermissionRequester(
    permission: String,
    onPermissionDenied: () -> Unit = {},
    onPermissionGranted: () -> Unit = {},
    showRequestButton: Boolean = true
) {
    val context = LocalContext.current
    var showSettings by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    var isGranted by remember {
        mutableStateOf(isPermissionGranted(context, permission))
    }
    var launcherInitialized by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (granted) {
            onPermissionGranted()
            showSettings = false
            showRationale = false
        } else {
            val activity = context.findActivity()
            val shouldShowRationale =
                activity?.shouldShowRequestPermissionRationale(permission) == true
            showRationale = shouldShowRationale
            if (!shouldShowRationale) {
                showSettings = true
            }
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        launcherInitialized = true
        val currentGranted = isPermissionGranted(context, permission)
        if (currentGranted != isGranted) {
            isGranted = currentGranted
            if (currentGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        launcherInitialized = true
        val currentGranted = isPermissionGranted(context, permission)
        if (currentGranted != isGranted) {
            isGranted = currentGranted
            if (currentGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        onPauseOrDispose {
        }
    }

    LaunchedEffect(launcherInitialized) {
        if (launcherInitialized && !isGranted) {
            requestPermissionLauncher.launch(permission)
        }
    }

    val openSettingsIntent = remember {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            title = { Text("Permission Required") },
            text = {
                Text(
                    "Location permission is required for this feature. " +
                            "Please enable it in the app settings."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSettings = false
                        context.startActivity(openSettingsIntent)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (!isGranted && showRequestButton) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showRationale) {
                Text(
                    "This feature requires location permission to work properly.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (showRationale) {
                        showSettings = true
                    } else if (launcherInitialized) {
                        requestPermissionLauncher.launch(permission)
                    }
                }
            ) {
                Text(if (showRationale) "Open Settings" else "Request Permission")
            }
        }
    }
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}
