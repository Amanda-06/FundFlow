// feature/profile/domain/usecase/UpdatePasswordUseCase.kt
package com.example.fundflow.feature.profile.domain.usecase

import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    /**
     * @param passwordSaatIni  Password lama — dikirim ke Firebase untuk re-autentikasi
     * @param passwordBaru     Password baru yang akan disimpan
     */
    suspend operator fun invoke(passwordSaatIni: String, passwordBaru: String) {
        require(passwordSaatIni.isNotBlank()) { "Password saat ini tidak boleh kosong" }
        require(passwordBaru.length >= 6)     { "Password baru minimal 6 karakter" }
        repository.updatePassword(passwordSaatIni, passwordBaru)
    }
}