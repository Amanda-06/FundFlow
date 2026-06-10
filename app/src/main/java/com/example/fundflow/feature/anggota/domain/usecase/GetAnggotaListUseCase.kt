// feature/anggota/domain/usecase/GetAnggotaListUseCase.kt
// ============================================================
package com.example.fundflow.feature.anggota.domain.usecase

import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnggotaListUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    operator fun invoke(): Flow<List<Anggota>> = repository.getAll()
}
