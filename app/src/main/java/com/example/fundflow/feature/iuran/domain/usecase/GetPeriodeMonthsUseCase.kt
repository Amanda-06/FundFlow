package com.example.fundflow.feature.iuran.domain.usecase

import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Generate daftar bulan ("yyyy-MM" + label tampilan) berdasarkan
 * periode kas yang sudah diset user saat register / pengaturan periode.
 * Jika periode belum diset, fallback ke 12 bulan tahun berjalan.
 */
class GetPeriodeMonthsUseCase @Inject constructor(
    private val periodeDao: PeriodeDao
) {
    suspend operator fun invoke(userId: String): List<MonthOption> {
        val periode = periodeDao.getPeriodeByUserId(userId)

        val (startStr, endStr) = if (periode != null) {
            periode.tanggalMulai to periode.tanggalSelesai
        } else {
            val year = LocalDate.now().year
            "$year-01" to "$year-12"
        }

        val start = LocalDate.parse("$startStr-01")
        val end   = LocalDate.parse("$endStr-01")

        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
        val result = mutableListOf<MonthOption>()
        var current = start
        while (!current.isAfter(end)) {
            result.add(
                MonthOption(
                    key   = current.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    label = current.format(formatter).replaceFirstChar { it.uppercase() },
                    bulan = current.monthValue,
                    tahun = current.year
                )
            )
            current = current.plusMonths(1)
        }
        return result
    }
}

data class MonthOption(
    val key: String,    // "yyyy-MM"
    val label: String,  // "April 2026"
    val bulan: Int,
    val tahun: Int
)