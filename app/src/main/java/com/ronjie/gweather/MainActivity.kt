package com.ronjie.gweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.navigation.Screen
import com.ronjie.gweather.presentation.component.GlobalSnackbar
import com.ronjie.gweather.presentation.component.MessageManager
import com.ronjie.gweather.presentation.component.rememberMessageManager
import com.ronjie.gweather.presentation.screen.auth.AuthScreen
import com.ronjie.gweather.presentation.screen.auth.AuthState
import com.ronjie.gweather.presentation.screen.auth.AuthViewModel
import com.ronjie.gweather.presentation.screen.weather.WeatherScreen
import com.ronjie.gweather.presentation.screen.weather.WeatherViewModel
import com.ronjie.gweather.presentation.theme.GWeatherTheme
import com.ronjie.gweather.utils.LocationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

val LocalMessageManager = compositionLocalOf<MessageManager> { error("No MessageManager provided") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider
    private val auth = FirebaseAuth.getInstance()
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationProvider = LocationProvider(this)

        setContent {
            val messageManager = rememberMessageManager()
            val currentLocation by locationProvider.locationUpdates.collectAsStateWithLifecycle(
                initialValue = Coordinates(0.0, 0.0)
            )
            GWeatherTheme {
                val navController = rememberNavController()
                val startDestination = if (auth.currentUser != null) {
                    Screen.WEATHER_ROUTE
                } else {
                    Screen.AUTH_ROUTE
                }

                LaunchedEffect(Unit) {
                    if (!locationProvider.hasLocationPermission()) {
                        locationProvider.requestLocationPermissions()
                    } else {
                        weatherViewModel.loadWeather(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    }
                }

                CompositionLocalProvider(
                    LocalMessageManager provides messageManager
                ) {
                    Scaffold { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination,
                                weatherViewModel = weatherViewModel,
                                currentLocation = currentLocation,
                                locationProvider = locationProvider,
                                onSignOut = {
                                    auth.signOut()
                                    navController.navigate(Screen.AUTH_ROUTE) {
                                        popUpTo(Screen.WEATHER_ROUTE) { inclusive = true }
                                    }
                                }
                            )
                            GlobalSnackbar(messageManager = messageManager)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    weatherViewModel: WeatherViewModel,
    currentLocation: Coordinates,
    locationProvider: LocationProvider,
    onSignOut: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.AUTH_ROUTE) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()
            val messageManager = LocalMessageManager.current
            val scope = rememberCoroutineScope()

            LaunchedEffect(authState) {
                when (val state = authState) {
                    is AuthState.Success -> {
                        navController.navigate(Screen.WEATHER_ROUTE) {
                            popUpTo(Screen.AUTH_ROUTE) { inclusive = true }
                        }
                    }

                    is AuthState.Error -> {
                        scope.launch {
                            messageManager.showMessage(state.message, isError = true)
                        }
                    }

                    else -> {}
                }
            }

            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.WEATHER_ROUTE) {
                        popUpTo(Screen.AUTH_ROUTE) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.WEATHER_ROUTE) {
            val messageManager = LocalMessageManager.current
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                if (!locationProvider.hasLocationPermission()) {
                    locationProvider.requestLocationPermissions()
                }
            }

            LaunchedEffect(Unit) {
                if (!locationProvider.hasLocationPermission()) {
                    locationProvider.requestLocationPermissions()
                } else if (currentLocation.latitude != 0.0 && currentLocation.longitude != 0.0) {
                    weatherViewModel.loadWeather(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                }
            }

            WeatherScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = weatherViewModel,
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude,
                onSignOut = onSignOut,
                onError = { error ->
                    scope.launch {
                        messageManager.showMessage(error, isError = true)
                    }
                }
            )
        }
    }
}
