package com.example.fundflow.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Retrofit singleton untuk Nager.Date Public Holiday API.
 * Base URL : https://date.nager.at/api/v3/
 * Disediakan sebagai dependency oleh NetworkModule (Hilt).
 */
object ApiClient {

    private const val BASE_URL = "https://date.nager.at/api/v3/"
    private const val TIMEOUT_SECONDS = 15L

    // Kotlinx Serialization — toleran terhadap key yang tidak dikenal
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient          = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
