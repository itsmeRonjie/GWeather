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

    override suspend fun saveLastLocation(latitude: Double, longitude: Double) {
        val location = LastLocationEntity(latitude = latitude, longitude = longitude)
        lastLocationDao.insertOrUpdate(location)
    }
}