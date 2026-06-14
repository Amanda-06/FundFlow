package com.example.fundflow.feature.anggota.domain.usecase

import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import javax.inject.Inject

class UpdateAnggotaUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    suspend operator fun invoke(anggota: Anggota) = repository.update(anggota)
}
