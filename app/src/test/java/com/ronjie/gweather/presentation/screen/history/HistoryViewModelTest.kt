package com.ronjie.gweather.presentation.screen.history

import com.ronjie.gweather.data.mapper.toDomain
import com.ronjie.gweather.domain.repository.WeatherRepository
import com.ronjie.gweather.utils.TestData.createTestWeatherEntity
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockWeatherRepository = mockk<WeatherRepository>()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearMocks(mockWeatherRepository)
        coEvery { mockWeatherRepository.getWeatherHistory() } returns flowOf(emptyList())
        viewModel = HistoryViewModel(mockWeatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val flowEmitted = CompletableDeferred<Unit>()

        coEvery { mockWeatherRepository.getWeatherHistory() } returns flow {
            flowEmitted.await()
            emit(emptyList())
        }

        val testViewModel = HistoryViewModel(mockWeatherRepository)

        assertTrue(
            "Initial state should be Loading",
            testViewModel.uiState.value is HistoryUiState.Loading
        )

        flowEmitted.complete(Unit)
        testScheduler.advanceUntilIdle()

        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherHistory with empty list shows empty state`() = runTest {
        coEvery { mockWeatherRepository.getWeatherHistory() } returns flowOf(emptyList())

        viewModel = HistoryViewModel(mockWeatherRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Empty but was $state", state is HistoryUiState.Empty)
    }

    @Test
    fun `loadWeatherHistory with data shows success state`() = runTest {
        val testWeatherEntity = createTestWeatherEntity()
        val testWeather = listOf(testWeatherEntity)

        coEvery { mockWeatherRepository.getWeatherHistory() } returns flow {
            emit(testWeather)
        }

        val viewModel = HistoryViewModel(mockWeatherRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value

        if (state !is HistoryUiState.Success) {
            if (state is HistoryUiState.Error) {
                println("Error message: ${state.message}")
            }
            assertTrue("State should be Success but was $state", false)
            return@runTest
        }

        val successState = state


        assertEquals("Should contain one weather item", 1, successState.weatherList.size)

        val actualWeather = successState.weatherList.firstOrNull()
            ?: throw AssertionError("Weather list is empty")

        val expectedWeather = testWeatherEntity.toDomain()

        assertEquals(
            "Location name should match",
            expectedWeather.location,
            actualWeather.location
        )

        assertEquals(
            "Weather ID should match",
            testWeatherEntity.id,
            actualWeather.weatherId.toLong()
        )
    }

    @Test
    fun `loadWeatherHistory with error shows error state`() = runTest {
        val errorMessage = "Test error message"
        coEvery { mockWeatherRepository.getWeatherHistory() } returns flow {
            throw Exception(errorMessage)
        }

        val testViewModel = HistoryViewModel(mockWeatherRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = testViewModel.uiState.value
        assertTrue("State should be Error", state is HistoryUiState.Error)

        val errorState = state as HistoryUiState.Error
        assertEquals("Error message should match", errorMessage, errorState.message)
    }

    @Test
    fun `loadWeatherHistory collects from repository`() = runTest {
        val testWeather = listOf(createTestWeatherEntity())
        coEvery { mockWeatherRepository.getWeatherHistory() } returns flowOf(testWeather)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockWeatherRepository.getWeatherHistory() }
    }
}
