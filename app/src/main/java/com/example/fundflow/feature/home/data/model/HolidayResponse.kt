package com.example.fundflow.feature.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("date")        val date: String,
    @SerialName("localName")   val localName: String,
    @SerialName("name")        val name: String,
    @SerialName("countryCode") val countryCode: String,
    @SerialName("fixed")       val fixed: Boolean = false,
    @SerialName("global")      val global: Boolean = true,
    @SerialName("types")       val types: List<String> = emptyList()
)