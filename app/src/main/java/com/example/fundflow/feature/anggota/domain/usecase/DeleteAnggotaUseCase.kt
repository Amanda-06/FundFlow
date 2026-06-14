package com.example.fundflow.feature.anggota.domain.usecase

import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import javax.inject.Inject

class DeleteAnggotaUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    /** Hapus satu anggota. Cascade ke tabel iuran otomatis via Room FK. */
    suspend operator fun invoke(id: Int) = repository.delete(id)
}


