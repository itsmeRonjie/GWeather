package com.ronjie.gweather.domain.usecase

import com.ronjie.gweather.domain.repository.WeatherRepository
import com.ronjie.gweather.utils.TestData.createTestWeather
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class GetCurrentWeatherUseCaseTest {

    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private val mockRepository: WeatherRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        getCurrentWeatherUseCase = GetCurrentWeatherUseCase(mockRepository)
    }

    @Test
    fun `invoke should return success result when repository call is successful`() = runTest {
        val latitude = 37.7749
        val longitude = -122.4194
        val saveToDatabase = true
        val expectedWeather = createTestWeather()

        coEvery {
            mockRepository.getCurrentWeather(latitude, longitude, saveToDatabase)
        } returns Result.success(expectedWeather)

        val result = getCurrentWeatherUseCase(latitude, longitude, saveToDatabase)

        assertEquals(true, result.isSuccess)
        assertEquals(expectedWeather, result.getOrNull())
        coVerify(exactly = 1) {
            mockRepository.getCurrentWeather(latitude, longitude, saveToDatabase)
        }
    }

    @Test
    fun `invoke should return failure result when repository call fails`() = runTest {
        val latitude = 37.7749
        val longitude = -122.4194
        val saveToDatabase = false
        val expectedException = IOException("Network error")

        coEvery {
            mockRepository.getCurrentWeather(latitude, longitude, saveToDatabase)
        } returns Result.failure(expectedException)

        val result = getCurrentWeatherUseCase(latitude, longitude, saveToDatabase)

        assertEquals(true, result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 1) {
            mockRepository.getCurrentWeather(latitude, longitude, saveToDatabase)
        }
    }
}
