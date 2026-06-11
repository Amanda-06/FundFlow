package com.example.fundflow.feature.settings.domain.usecase

import com.example.fundflow.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(lang: String) = repository.setLanguage(lang)
}