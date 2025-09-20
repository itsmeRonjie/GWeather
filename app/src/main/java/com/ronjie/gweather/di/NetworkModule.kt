package com.ronjie.gweather.di

import com.ronjie.gweather.BuildConfig
import com.ronjie.gweather.data.remote.WeatherApi
import com.ronjie.gweather.data.remote.WeatherRemoteDataSource
import com.ronjie.gweather.data.repository.WeatherLocalDataSource
import com.ronjie.gweather.data.repository.WeatherRepositoryImpl
import com.ronjie.gweather.data.util.Constants
import com.ronjie.gweather.domain.repository.WeatherRepository
import com.ronjie.gweather.domain.usecase.GetCurrentWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "${Constants.API_BASE_URL}/data/2.5/"

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(
        weatherApi: WeatherApi
    ): WeatherRemoteDataSource {
        return WeatherRemoteDataSource(weatherApi)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        localDataSource: WeatherLocalDataSource,
        remoteDataSource: WeatherRemoteDataSource,
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(
        repository: WeatherRepository
    ): GetCurrentWeatherUseCase {
        return GetCurrentWeatherUseCase(repository)
    }
}
