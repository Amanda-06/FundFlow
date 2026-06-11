package com.example.fundflow.feature.settings.domain.usecase

import com.example.fundflow.feature.settings.domain.model.AppSettings
import com.example.fundflow.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.observeSettings()
}