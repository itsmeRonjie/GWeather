package com.ronjie.gweather.presentation

import android.util.Log
import com.ronjie.gweather.data.local.entity.LastLocationEntity
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.utils.LocationProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockLocationProvider: LocationProvider

    @MockK
    private lateinit var mockLocationRepository: LocationRepository

    private val testDispatcher = UnconfinedTestDispatcher()
    private val locationUpdates = MutableSharedFlow<Coordinates>()

    private fun setupMocks() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        setupMocks()

        coEvery { mockLocationProvider.locationUpdates } returns locationUpdates
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initialization loads last saved location`() = runTest {
        val testLocation = LastLocationEntity(
            id = 1,
            latitude = 1.23,
            longitude = 4.56
        )
        coEvery { mockLocationRepository.getLastSavedLocationOnce() } returns testLocation

        val viewModel = MainViewModel(mockLocationProvider, mockLocationRepository)

        assertEquals(
            Coordinates(testLocation.latitude, testLocation.longitude),
            viewModel.currentLocation.value
        )
        coVerify { mockLocationRepository.getLastSavedLocationOnce() }
    }

    @Test
    fun `initialization handles error when loading last location`() = runTest {
        coEvery { mockLocationRepository.getLastSavedLocationOnce() } throws Exception("Test error")

        val viewModel = MainViewModel(mockLocationProvider, mockLocationRepository)

        assertEquals(Coordinates(0.0, 0.0), viewModel.currentLocation.value)
    }

    @Test
    fun `location updates are received and saved`() = runTest {
        val testCoordinates = Coordinates(12.34, 56.78)
        val viewModel = MainViewModel(mockLocationProvider, mockLocationRepository)

        locationUpdates.emit(testCoordinates)

        assertEquals(testCoordinates, viewModel.currentLocation.value)
        coVerify {
            mockLocationRepository.saveLastLocation(
                latitude = testCoordinates.latitude,
                longitude = testCoordinates.longitude
            )
        }
    }

    @Test
    fun `updateLocation updates current location and saves it`() = runTest {
        val newCoordinates = Coordinates(98.76, 54.32)
        coEvery {
            mockLocationRepository.saveLastLocation(
                latitude = newCoordinates.latitude,
                longitude = newCoordinates.longitude
            )
        } returns Unit

        val viewModel = MainViewModel(mockLocationProvider, mockLocationRepository)

        viewModel.updateLocation(newCoordinates)

        assertEquals(newCoordinates, viewModel.currentLocation.value)
        coVerify {
            mockLocationRepository.saveLastLocation(
                latitude = newCoordinates.latitude,
                longitude = newCoordinates.longitude
            )
        }
    }

    @Test
    fun `error when saving location is caught and logged`() = runTest {
        val testError = Exception("Test error")
        val testCoordinates = Coordinates(1.0, 2.0)

        coEvery {
            mockLocationRepository.saveLastLocation(
                latitude = testCoordinates.latitude,
                longitude = testCoordinates.longitude
            )
        } throws testError

        every { Log.e(any(), any(), any<Throwable>()) } returns 0

        val viewModel = MainViewModel(mockLocationProvider, mockLocationRepository)

        viewModel.updateLocation(testCoordinates)

        verify {
            Log.e(
                "MainViewModel",
                "Error saving location to database",
                match { it.message == testError.message }
            )
        }

        assertEquals(testCoordinates, viewModel.currentLocation.value)
    }
}
