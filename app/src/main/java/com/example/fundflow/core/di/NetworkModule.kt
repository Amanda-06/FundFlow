package com.example.fundflow.core.di

import com.example.fundflow.core.network.ApiClient
import com.example.fundflow.feature.home.data.remote.HolidayApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return ApiClient.retrofit
    }

    @Provides
    @Singleton
    fun provideHolidayApiService(retrofit: Retrofit): HolidayApiService {
        return retrofit.create(HolidayApiService::class.java)
    }
}
