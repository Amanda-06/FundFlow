// ============================================================
// feature/home/data/remote/HolidayApiService.kt
// ============================================================
package com.example.fundflow.feature.home.data.remote

import com.example.fundflow.feature.home.data.model.HolidayResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HolidayApiService {
    /**
     * Ambil hari libur nasional berdasarkan tahun dan kode negara.
     * Contoh: GET https://date.nager.at/api/v3/PublicHolidays/2026/ID
     */
    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year")        year: Int,
        @Path("countryCode") countryCode: String = "ID"
    ): List<HolidayResponse>
}

