package com.example.fundflow.feature.profile.domain.usecase

import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    /** Observe profil secara realtime dari Room */
    operator fun invoke(): Flow<Profile?> = repository.observeProfile()

    /** Ambil profil sekali (one-shot), berguna untuk pre-fill form edit */
    suspend fun once(): Profile? = repository.getProfile()
}