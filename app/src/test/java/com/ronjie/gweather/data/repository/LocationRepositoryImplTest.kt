package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.local.dao.LastLocationDao
import com.ronjie.gweather.data.local.entity.LastLocationEntity
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
class LocationRepositoryImplTest {

    private lateinit var repository: LocationRepositoryImpl
    private val mockLastLocationDao: LastLocationDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = LocationRepositoryImpl(mockLastLocationDao)
    }

    @Test
    fun `getLastSavedLocationFlow returns flow from dao`() = runTest {
        val testLocation = createTestLocationEntity()
        every { mockLastLocationDao.getLastLocation() } returns flowOf(testLocation)

        val result = repository.getLastSavedLocationFlow()

        assertEquals(testLocation, result.first())
        verify { mockLastLocationDao.getLastLocation() }
    }

    @Test
    fun `getLastSavedLocationOnce returns location from dao`() = runTest {
        val testLocation = createTestLocationEntity()
        coEvery { mockLastLocationDao.getLastLocationOnce() } returns testLocation

        val result = repository.getLastSavedLocationOnce()

        assertEquals(testLocation, result)
        coVerify { mockLastLocationDao.getLastLocationOnce() }
    }

    @Test
    fun `saveLastLocation with valid coordinates saves to dao`() = runTest {
        val latitude = 40.7128
        val longitude = -74.0060

        repository.saveLastLocation(latitude, longitude)

        coVerify {
            mockLastLocationDao.insertOrUpdate(
                match {
                    it.id == 1 &&
                            it.latitude == latitude &&
                            it.longitude == longitude
                }
            )
        }
    }

    @Test
    fun `saveLastLocation with invalid coordinates does not save`() = runTest {
        val invalidLatitude = 100.0
        val invalidLongitude = 200.0

        repository.saveLastLocation(invalidLatitude, invalidLongitude)

        coVerify(exactly = 0) { mockLastLocationDao.insertOrUpdate(any()) }
    }

    @Test
    fun `saveLastLocation with zero coordinates does not save`() = runTest {
        repository.saveLastLocation(0.0, 0.0)

        coVerify(exactly = 0) { mockLastLocationDao.insertOrUpdate(any()) }
    }

    private fun createTestLocationEntity(): LastLocationEntity {
        return LastLocationEntity(
            id = 1,
            latitude = 40.7128,
            longitude = -74.0060
        )
    }
}
