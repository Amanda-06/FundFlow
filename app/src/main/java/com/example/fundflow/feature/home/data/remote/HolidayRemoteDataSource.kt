// ============================================================
// feature/home/data/remote/HolidayRemoteDataSource.kt
// ============================================================
package com.example.fundflow.feature.home.data.remote

import com.example.fundflow.core.network.SafeApiCall
import com.example.fundflow.core.util.Resource
import com.example.fundflow.feature.home.data.model.HolidayResponse
import javax.inject.Inject

class HolidayRemoteDataSource @Inject constructor(
    private val apiService: HolidayApiService
) {
    suspend fun getPublicHolidays(year: Int): Resource<List<HolidayResponse>> =
        safeApiCall { apiService.getPublicHolidays(year, "ID") }
}

