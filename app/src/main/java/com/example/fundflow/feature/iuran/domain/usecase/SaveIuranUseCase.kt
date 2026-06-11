package com.example.fundflow.feature.iuran.domain.usecase

import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import javax.inject.Inject

class SaveIuranUseCase @Inject constructor(
    private val repository: IuranRepository
) {
    suspend operator fun invoke(iuran: Iuran) {
        // Jika status belum bayar, reset nominal & metode agar data konsisten
        val cleaned = if (!iuran.statusBayar) {
            iuran.copy(nominal = 0.0, metodePembayaran = "", tanggalBayar = null, terlambat = false)
        } else iuran
        repository.saveIuran(cleaned)
    }
}