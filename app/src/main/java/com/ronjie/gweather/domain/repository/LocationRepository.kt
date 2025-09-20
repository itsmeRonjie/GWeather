package com.ronjie.gweather.domain.repository

import com.ronjie.gweather.data.local.entity.LastLocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLastSavedLocationFlow(): Flow<LastLocationEntity?>
    suspend fun getLastSavedLocationOnce(): LastLocationEntity?
    suspend fun saveLastLocation(latitude: Double, longitude: Double)
}
