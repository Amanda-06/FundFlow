// ============================================================
// feature/home/data/model/HolidayResponse.kt
// ============================================================
package com.example.fundflow.feature.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("date")        val date: String,          // "2026-01-01"
    @SerialName("localName")   val localName: String,     // "Tahun Baru"
    @SerialName("name")        val name: String,          // "New Year's Day"
    @SerialName("countryCode") val countryCode: String,   // "ID"
    @SerialName("fixed")       val fixed: Boolean = false,
    @SerialName("global")      val global: Boolean = true,
    @SerialName("types")       val types: List<String> = emptyList()
)
