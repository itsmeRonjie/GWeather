package com.ronjie.gweather.di

import android.content.Context
import com.ronjie.gweather.data.local.dao.LastLocationDao
import com.ronjie.gweather.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideLastLocationDao(appDatabase: AppDatabase): LastLocationDao {
        return appDatabase.lastLocationDao()
    }
}
