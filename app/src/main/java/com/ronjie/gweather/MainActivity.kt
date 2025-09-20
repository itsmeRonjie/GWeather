package com.ronjie.gweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.navigation.Screen
import com.ronjie.gweather.presentation.MainViewModel
import com.ronjie.gweather.presentation.component.GlobalSnackbar
import com.ronjie.gweather.presentation.component.MessageManager
import com.ronjie.gweather.presentation.component.rememberMessageManager
import com.ronjie.gweather.presentation.screen.auth.AuthScreen
import com.ronjie.gweather.presentation.screen.auth.AuthState
import com.ronjie.gweather.presentation.screen.auth.AuthViewModel
import com.ronjie.gweather.presentation.screen.history.HistoryScreen
import com.ronjie.gweather.presentation.screen.weather.WeatherScreen
import com.ronjie.gweather.presentation.screen.weather.WeatherViewModel
import com.ronjie.gweather.presentation.theme.GWeatherTheme
import com.ronjie.gweather.utils.LocationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

val LocalMessageManager = compositionLocalOf<MessageManager> { error("No MessageManager provided") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var locationProvider: LocationProvider
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationProvider.initialize(this)

        if (!locationProvider.hasLocationPermission()) {
            locationProvider.requestLocationPermissions()
        }

        setContent {
            GWeatherTheme {
                val navController = rememberNavController()
                val startDestination = if (auth.currentUser != null) {
                    Screen.WEATHER_ROUTE
                } else {
                    Screen.AUTH_ROUTE
                }

                val messageManager = rememberMessageManager()
                val mainViewModel: MainViewModel = hiltViewModel()
                val currentLocation by mainViewModel.currentLocation.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()

                CompositionLocalProvider(
                    LocalMessageManager provides messageManager
                ) {
                    MainScreen(
                        navController = navController,
                        startDestination = startDestination,
                        currentLocation = currentLocation,
                        locationProvider = locationProvider,
                        onSignOut = {
                            scope.launch {
                                auth.signOut()
                                navController.navigate(Screen.AUTH_ROUTE) {
                                    popUpTo(Screen.WEATHER_ROUTE) { inclusive = true }
                                }
                            }
                        },
                        messageManager = messageManager
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    startDestination: String,
    currentLocation: Coordinates,
    locationProvider: LocationProvider,
    onSignOut: () -> Unit,
    messageManager: MessageManager
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val currentLocationState by mainViewModel.currentLocation.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.WEATHER_ROUTE
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.AUTH_ROUTE) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val screenRoute = screen.route
                        NavigationBarItem(
                            icon = {
                                when (screen) {
                                    is Screen.Weather -> Icon(
                                        Icons.Default.Home,
                                        contentDescription = "Weather"
                                    )

                                    is Screen.History -> Icon(
                                        Icons.Default.History,
                                        contentDescription = "History"
                                    )

                                    else -> {}
                                }
                            },
                            label = { Text(screen.title) },
                            selected = currentRoute == screenRoute,
                            onClick = {
                                if (currentRoute != screenRoute) {
                                    navController.navigate(screenRoute) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.AUTH_ROUTE) {
                val authViewModel = hiltViewModel<AuthViewModel>()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()

                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        navController.navigate(Screen.WEATHER_ROUTE) {
                            popUpTo(Screen.AUTH_ROUTE) { inclusive = true }
                        }
                    }
                }

                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.WEATHER_ROUTE) {
                            popUpTo(Screen.AUTH_ROUTE) { inclusive = true }
                        }
                    },
                    onError = { error ->
                        scope.launch {
                            messageManager.showMessage(error, isError = true)
                        }
                    }
                )
            }

            composable(Screen.WEATHER_ROUTE) {
                val weatherViewModel = hiltViewModel<WeatherViewModel>()

                LaunchedEffect(currentLocationState) {
                    weatherViewModel.loadWeather(
                        currentLocationState.latitude,
                        currentLocationState.longitude
                    )
                }

                LaunchedEffect(Unit) {
                    locationProvider.locationUpdates.collect { location ->
                        mainViewModel.updateLocation(location)
                    }
                }

                WeatherScreen(
                    modifier = Modifier,
                    viewModel = weatherViewModel,
                    latitude = currentLocationState.latitude,
                    longitude = currentLocationState.longitude,
                    onError = { error ->
                        scope.launch {
                            messageManager.showMessage(error, isError = true)
                        }
                    }
                )
            }
            composable(Screen.HISTORY_ROUTE) {
                HistoryScreen()
            }
        }
        
        val currentMessage by messageManager.currentMessage.collectAsState()
        currentMessage?.let { message ->
            GlobalSnackbar(
                message = message.message,
                isError = message.isError,
                onDismiss = { messageManager.dismissMessage() }
            )
        }
    }
}
