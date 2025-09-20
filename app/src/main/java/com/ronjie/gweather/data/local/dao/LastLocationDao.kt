package com.ronjie.gweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ronjie.gweather.data.local.entity.LastLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(lastLocation: LastLocationEntity)

    @Query("SELECT * FROM last_location WHERE id = 1")
    fun getLastLocation(): Flow<LastLocationEntity?>

    @Query("SELECT * FROM last_location WHERE id = 1")
    suspend fun getLastLocationOnce(): LastLocationEntity?
}