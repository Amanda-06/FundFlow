// ============================================================
// feature/home/data/mapper/HolidayMapper.kt
// ============================================================
package com.example.fundflow.feature.home.data.mapper

import com.example.fundflow.feature.home.data.model.HolidayResponse
import com.example.fundflow.feature.home.domain.model.Holiday

fun HolidayResponse.toDomain(): Holiday = Holiday(
    date      = date,
    localName = localName,
    name      = name
)

fun List<HolidayResponse>.toDomainList(): List<Holiday> = map { it.toDomain() }

