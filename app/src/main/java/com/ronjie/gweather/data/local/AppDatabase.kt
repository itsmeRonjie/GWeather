package com.ronjie.gweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ronjie.gweather.data.local.converters.CoordinatesConverter
import com.ronjie.gweather.data.local.dao.LastLocationDao
import com.ronjie.gweather.data.local.dao.WeatherDao
import com.ronjie.gweather.data.local.entity.LastLocationEntity
import com.ronjie.gweather.data.local.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class, LastLocationEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(CoordinatesConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    abstract fun lastLocationDao(): LastLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gweather_database"
                ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
