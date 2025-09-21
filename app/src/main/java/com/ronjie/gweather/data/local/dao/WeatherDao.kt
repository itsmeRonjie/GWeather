package com.ronjie.gweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ronjie.gweather.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC")
    fun getAllWeatherHistory(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather_history WHERE id = :id")
    suspend fun getWeatherById(id: Long): WeatherEntity?

    @Query("SELECT * FROM weather_history WHERE location LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchWeatherHistory(query: String): Flow<List<WeatherEntity>>
}
