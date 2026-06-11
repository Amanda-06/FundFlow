package com.example.fundflow.feature.settings.domain.usecase

import com.example.fundflow.feature.settings.domain.model.PeriodeKas
import com.example.fundflow.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdatePeriodeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(periode: PeriodeKas) {
        require(periode.bulanMulai.isNotBlank())   { "Periode mulai tidak boleh kosong" }
        require(periode.bulanSelesai.isNotBlank()) { "Periode selesai tidak boleh kosong" }
        require(periode.bulanMulai <= periode.bulanSelesai) { "Periode mulai harus sebelum periode selesai" }
        repository.updatePeriode(periode)
    }
}