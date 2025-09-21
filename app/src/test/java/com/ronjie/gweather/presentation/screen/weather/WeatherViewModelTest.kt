package com.ronjie.gweather.presentation.screen.weather

import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.domain.repository.WeatherRepository
import com.ronjie.gweather.domain.usecase.GetCurrentWeatherUseCase
import com.ronjie.gweather.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockGetCurrentWeather = mockk<GetCurrentWeatherUseCase>()
    private val mockWeatherRepository = mockk<WeatherRepository>()
    private val mockLocationRepository = mockk<LocationRepository>()

    private lateinit var viewModel: WeatherViewModel

    private val testWeather = TestData.createTestWeather()
    private val testLocation = TestData.createTestLocationEntity()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { mockLocationRepository.getLastSavedLocationOnce() } returns testLocation
        coEvery { mockWeatherRepository.getCachedWeather() } returns flowOf(testWeather)
        coEvery { mockGetCurrentWeather(any(), any(), any()) } returns Result.success(testWeather)
        coEvery { mockLocationRepository.saveLastLocation(any(), any()) } returns Unit

        viewModel = WeatherViewModel(
            getCurrentWeather = mockGetCurrentWeather,
            weatherRepository = mockWeatherRepository,
            locationRepository = mockLocationRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial load with saved location loads weather`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Expected Success state", viewModel.uiState.value is WeatherUiState.Success)

        val successState = viewModel.uiState.value as WeatherUiState.Success
        assertEquals(testWeather, successState.weather)

        coVerify {
            mockGetCurrentWeather(
                testLocation.latitude,
                testLocation.longitude,
                true
            )
        }

        coVerify {
            mockLocationRepository.saveLastLocation(
                testLocation.latitude,
                testLocation.longitude
            )
        }
    }

    @Test
    fun `initial load without saved location shows error`() = runTest {
        coEvery { mockLocationRepository.getLastSavedLocationOnce() } returns null

        coEvery { mockWeatherRepository.getCachedWeather() } returns flowOf(null)
        val viewModel = WeatherViewModel(
            getCurrentWeather = mockGetCurrentWeather,
            weatherRepository = mockWeatherRepository,
            locationRepository = mockLocationRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            "Expected Error state but was ${state::class.simpleName}",
            state is WeatherUiState.Error
        )

        val errorState = state as WeatherUiState.Error
        assertEquals(
            "Error message should match",
            "No location saved. Please set your location.",
            errorState.message
        )
    }

    @Test
    fun `loadWeather with new coordinates updates location and loads weather`() = runTest {
        val testLat = 37.7749
        val testLon = -122.4194

        viewModel.loadWeather(testLat, testLon, saveToDatabase = true)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockLocationRepository.saveLastLocation(testLat, testLon) }

        coVerify { mockGetCurrentWeather(testLat, testLon, true) }

        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
    }

    @Test
    fun `loadWeather with force refresh reloads weather`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadWeather(
            latitude = testLocation.latitude,
            longitude = testLocation.longitude,
            forceRefresh = true
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 2) {
            mockGetCurrentWeather(any(), any(), any())
        }
    }

    @Test
    fun `error loading weather falls back to cached data`() = runTest {
        coEvery { mockGetCurrentWeather(any(), any(), any()) } returns
                Result.failure(Exception("Network error"))

        val viewModel = WeatherViewModel(
            getCurrentWeather = mockGetCurrentWeather,
            weatherRepository = mockWeatherRepository,
            locationRepository = mockLocationRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
    }
}
