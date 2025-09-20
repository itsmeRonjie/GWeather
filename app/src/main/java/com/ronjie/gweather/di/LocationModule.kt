package com.ronjie.gweather.di

import android.content.Context
import com.ronjie.gweather.data.local.dao.LastLocationDao
import com.ronjie.gweather.data.repository.LocationRepositoryImpl
import com.ronjie.gweather.domain.repository.LocationRepository
import com.ronjie.gweather.utils.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object LocationModule {
    @Provides
    fun provideLocationProvider(
        @ApplicationContext context: Context
    ): LocationProvider {
        return LocationProvider(context)
    }

    @Provides
    fun provideLocationRepository(
        locationDao: LastLocationDao
    ): LocationRepository {
        return LocationRepositoryImpl(locationDao)
    }
}
