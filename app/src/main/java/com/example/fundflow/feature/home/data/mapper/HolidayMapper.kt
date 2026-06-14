package com.example.fundflow.feature.home.data.mapper

import com.example.fundflow.feature.home.data.model.HolidayResponse
import com.example.fundflow.feature.home.domain.model.Holiday
import java.util.Locale

fun HolidayResponse.toDomain(): Holiday {
    val currentLanguage = Locale.getDefault().language

    val finalHolidayName = if (currentLanguage == "en") name else localName
    val finalTypeName = if (currentLanguage == "en") "Public Holiday" else "Libur Nasional"

    return Holiday(
        date      = date,
        localName = finalHolidayName,
        name      = finalTypeName
    )
}

fun List<HolidayResponse>.toDomainList(): List<Holiday> = map { it.toDomain() }