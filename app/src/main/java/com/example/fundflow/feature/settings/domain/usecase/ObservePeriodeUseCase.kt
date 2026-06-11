package com.example.fundflow.feature.settings.domain.usecase

import com.example.fundflow.feature.settings.domain.model.PeriodeKas
import com.example.fundflow.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePeriodeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(userId: String): Flow<PeriodeKas?> = repository.observePeriode(userId)
}