// ============================================================
// feature/onboarding/presentation/OnboardingViewModel.kt
// ============================================================
package com.example.fundflow.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    /** Dipanggil saat user menekan tombol "Mulai" atau "Daftar Akun Baru".
     *  Tandai onboarding sudah dilihat agar tidak tampil lagi. */
    fun markOnboardingDone() {
        viewModelScope.launch {
            settingsDataStore.setHasSeenOnboarding(true)
        }
    }
}