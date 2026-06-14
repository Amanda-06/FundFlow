package com.example.fundflow.feature.home.data.mapper

import com.example.fundflow.feature.home.data.model.HolidayResponse
import com.example.fundflow.feature.home.domain.model.Holiday
import java.util.Locale

fun HolidayResponse.toDomain(): Holiday {
    // Jalankan pemeriksaan bahasa aplikasi yang sedang aktif secara real-time
    val currentLanguage = Locale.getDefault().language

    // PURE API MULTILANGUAGE: Memanfaatkan properti bawaan langsung dari respons Nager.Date
    val finalHolidayName = if (currentLanguage == "en") name else localName
    val finalTypeName = if (currentLanguage == "en") "Public Holiday" else "Libur Nasional"

    return Holiday(
        date      = date,
        localName = finalHolidayName, // Berubah otomatis: "Hari Kemerdekaan" atau "Independence Day"
        name      = finalTypeName   // Berubah otomatis: "Libur Nasional" atau "Public Holiday"
    )
}

fun List<HolidayResponse>.toDomainList(): List<Holiday> = map { it.toDomain() }