package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.mapper.toDTO
import com.ronjie.gweather.data.mapper.toDomain
import com.ronjie.gweather.data.model.CloudsDTO
import com.ronjie.gweather.data.model.CoordinatesDTO
import com.ronjie.gweather.data.model.MainDTO
import com.ronjie.gweather.data.model.SysDTO
import com.ronjie.gweather.data.model.WeatherDTO
import com.ronjie.gweather.data.model.WeatherResponseDTO
import com.ronjie.gweather.data.model.WindDTO
import com.ronjie.gweather.data.remote.WeatherRemoteDataSource
import com.ronjie.gweather.domain.model.Coordinates
import com.ronjie.gweather.utils.TestData.createTestWeather
import com.ronjie.gweather.utils.TestData.createTestWeatherEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherRepositoryImplTest {

    private lateinit var repository: WeatherRepositoryImpl
    private val mockRemoteDataSource: WeatherRemoteDataSource = mockk()
    private val mockLocalDataSource: WeatherLocalDataSource = mockk()
    private val testApiKey = "test_api_key"

    @Before
    fun setUp() {
        repository = WeatherRepositoryImpl(mockRemoteDataSource, mockLocalDataSource, testApiKey)
    }

    @Test
    fun `getCurrentWeather with valid coordinates returns success`() = runBlocking {
        val lat = 40.7128
        val lon = -74.0060
        val mockWeatherDTO = createTestWeatherDTO()
        val expectedWeather = mockWeatherDTO.toDomain()

        coEvery {
            mockRemoteDataSource.getCurrentWeather(
                lat,
                lon,
                testApiKey
            )
        } returns mockWeatherDTO
        coEvery { mockLocalDataSource.getWeatherHistory() } returns flowOf(emptyList())

        val result = repository.getCurrentWeather(lat, lon)

        assertTrue(result.isSuccess)
        assertEquals(expectedWeather.location, result.getOrNull()?.location)
        coVerify { mockRemoteDataSource.getCurrentWeather(lat, lon, testApiKey) }
    }

    @Test
    fun `getCurrentWeather with invalid coordinates returns failure`() = runBlocking {
        val invalidLat = 100.0
        val lon = -200.0

        val result = repository.getCurrentWeather(invalidLat, lon)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getCurrentWeather with network error returns failure`() = runBlocking {
        val lat = 40.7128
        val lon = -74.0060

        coEvery { mockRemoteDataSource.getCurrentWeather(lat, lon, testApiKey) } throws IOException(
            "Network error"
        )
        coEvery { mockLocalDataSource.getWeatherHistory() } returns flowOf(emptyList())

        val result = repository.getCurrentWeather(lat, lon)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `saveWeather with valid coordinates saves to local data source`() = runBlocking {
        val weather = createTestWeather()
        val locationName = "Test Location"
        val lat = 40.7128
        val lon = -74.0060

        coEvery { mockLocalDataSource.saveWeather(any(), any(), any(), any()) } returns Unit

        repository.saveWeather(weather, locationName, lat, lon)

        coVerify { mockLocalDataSource.saveWeather(weather.toDTO(), locationName, lat, lon) }
    }

    @Test
    fun `getCachedWeather returns flow of cached weather`() = runBlocking {
        val testWeather = createTestWeather()
        repository = WeatherRepositoryImpl(mockRemoteDataSource, mockLocalDataSource, testApiKey)

        coEvery {
            mockRemoteDataSource.getCurrentWeather(
                any(),
                any(),
                any()
            )
        } returns createTestWeatherDTO()
        coEvery { mockLocalDataSource.getWeatherHistory() } returns flowOf(emptyList())
        repository.getCurrentWeather(40.7128, -74.0060)

        val cachedWeather = repository.getCachedWeather().single()
        assertEquals(testWeather.location, cachedWeather?.location)
    }

    @Test
    fun `getWeatherHistory returns flow from local data source`() = runBlocking {
        val testEntities = listOf(createTestWeatherEntity())
        every { mockLocalDataSource.getWeatherHistory() } returns flowOf(testEntities)

        val result = repository.getWeatherHistory()

        assertEquals(testEntities, result.single())
        verify { mockLocalDataSource.getWeatherHistory() }
    }

    @Test
    fun `searchWeatherHistory returns filtered results from local data source`() = runBlocking {
        val query = "Test"
        val testEntities = listOf(createTestWeatherEntity())
        every { mockLocalDataSource.searchWeatherHistory(query) } returns flowOf(testEntities)

        val result = repository.searchWeatherHistory(query)

        assertEquals(testEntities, result.single())
        verify { mockLocalDataSource.searchWeatherHistory(query) }
    }

    @Test
    fun `getLastSavedLocationOnce returns coordinates from local data source`() = runBlocking {
        val testCoordinates = Coordinates(40.7128, -74.0060)
        val testEntity = createTestWeatherEntity().copy(
            coordinates = Coordinates(
                latitude = testCoordinates.latitude,
                longitude = testCoordinates.longitude
            ),
            timestamp = System.currentTimeMillis()
        )

        coEvery { mockLocalDataSource.getWeatherHistory() } returns flow { emit(listOf(testEntity)) }

        val result = repository.getLastSavedLocationOnce()

        assertEquals(testCoordinates, result)
        coVerify { mockLocalDataSource.getWeatherHistory() }
    }

    private fun createTestWeatherDTO(): WeatherResponseDTO {
        return WeatherResponseDTO(
            name = "Test Location",
            weatherDTO = listOf(WeatherDTO(0, "1", "desc", "10d")),
            mainDTO = MainDTO(25.0, 50.0, 1012.0, 5.0, 180, 0, 0),
            windDTO = WindDTO(5.0, 180),
            coordinatesDTO = CoordinatesDTO(40.7128, -74.0060),
            sysDTO = SysDTO(0, 0, "PH", 1758404022, 1758447694),
            visibility = 12,
            cloudsDTO = CloudsDTO(0),
            dateTime = 1758404022,
            timezone = 28800,
            id = 1,
            code = 200
        )
    }
}
