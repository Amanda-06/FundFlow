// ============================================================
// feature/anggota/domain/usecase/DeleteSelectedAnggotaUseCase.kt
// ============================================================
package com.example.fundflow.feature.anggota.domain.usecase

import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import javax.inject.Inject

class DeleteSelectedAnggotaUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    /** Hapus banyak anggota sekaligus (batch delete dari mode multi-select). */
    suspend operator fun invoke(ids: List<Int>) = repository.deleteSelected(ids)
}
