package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.local.dao.WeatherDao
import com.ronjie.gweather.utils.TestData.createTestWeatherDTO
import com.ronjie.gweather.utils.TestData.createTestWeatherEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherLocalDataSourceTest {

    private lateinit var dataSource: WeatherLocalDataSource
    private val mockWeatherDao: WeatherDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        dataSource = WeatherLocalDataSourceImpl(mockWeatherDao)
    }

    @Test
    fun `saveWeather calls insertWeather on dao`() = runTest {
        // Given
        val weatherDTO = createTestWeatherDTO()
        val locationName = "Test Location"
        val lat = 40.7128
        val lon = -74.0060

        // When
        dataSource.saveWeather(weatherDTO, locationName, lat, lon)

        // Then
        coVerify { mockWeatherDao.insertWeather(any()) }
    }

    @Test
    fun `getWeatherHistory returns flow from dao`() = runTest {
        // Given
        val testEntities = listOf(createTestWeatherEntity())
        every { mockWeatherDao.getAllWeatherHistory() } returns flowOf(testEntities)

        // When
        val result = dataSource.getWeatherHistory()

        // Then
        assertEquals(testEntities, result.first())
        verify { mockWeatherDao.getAllWeatherHistory() }
    }

    @Test
    fun `getWeatherById returns weather from dao`() = runTest {
        // Given
        val testId = 1L
        val testEntity = createTestWeatherEntity()
        coEvery { mockWeatherDao.getWeatherById(testId) } returns testEntity

        // When
        val result = dataSource.getWeatherById(testId)

        // Then
        assertEquals(testEntity, result)
        coVerify { mockWeatherDao.getWeatherById(testId) }
    }

    @Test
    fun `getWeatherById returns null when not found`() = runTest {
        // Given
        val testId = 1L
        coEvery { mockWeatherDao.getWeatherById(testId) } returns null

        // When
        val result = dataSource.getWeatherById(testId)

        // Then
        assertEquals(null, result)
        coVerify { mockWeatherDao.getWeatherById(testId) }
    }

    @Test
    fun `searchWeatherHistory returns filtered results from dao`() = runTest {
        // Given
        val query = "Test"
        val testEntities = listOf(createTestWeatherEntity())
        every { mockWeatherDao.searchWeatherHistory(query) } returns flowOf(testEntities)

        // When
        val result = dataSource.searchWeatherHistory(query)

        // Then
        assertEquals(testEntities, result.first())
        verify { mockWeatherDao.searchWeatherHistory(query) }
    }
}
