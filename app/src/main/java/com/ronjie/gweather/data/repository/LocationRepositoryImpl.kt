package com.ronjie.gweather.data.repository

import com.ronjie.gweather.data.local.dao.LastLocationDao
import com.ronjie.gweather.data.local.entity.LastLocationEntity
import com.ronjie.gweather.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val lastLocationDao: LastLocationDao
) : LocationRepository {
    override fun getLastSavedLocationFlow(): Flow<LastLocationEntity?> =
        lastLocationDao.getLastLocation()

    override suspend fun getLastSavedLocationOnce(): LastLocationEntity? =
        lastLocationDao.getLastLocationOnce()

    private fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 &&
                longitude in -180.0..180.0 &&
                (latitude != 0.0 || longitude != 0.0)
    }

    override suspend fun saveLastLocation(latitude: Double, longitude: Double) {
        if (!areValidCoordinates(latitude, longitude)) return

        val location = LastLocationEntity(
            id = 1,
            latitude = latitude,
            longitude = longitude
        )
        lastLocationDao.insertOrUpdate(location)
    }
}