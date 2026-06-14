package com.example.fundflow.feature.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("date")        val date: String,          // "2026-08-17"
    @SerialName("localName")   val localName: String,     // Dari API: "Hari Kemerdekaan"
    @SerialName("name")        val name: String,          // Dari API: "Independence Day"
    @SerialName("countryCode") val countryCode: String,   // "ID"
    @SerialName("fixed")       val fixed: Boolean = false,
    @SerialName("global")      val global: Boolean = true,
    @SerialName("types")       val types: List<String> = emptyList()
)