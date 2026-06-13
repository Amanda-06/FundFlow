package com.example.fundflow.feature.profile.presentation

import com.example.fundflow.feature.profile.domain.model.Profile

data class ProfileState(
    val profile: Profile?           = null,
    val isLoading: Boolean          = true,
    val showLogoutDialog: Boolean   = false,
    val isLoggingOut: Boolean       = false
)
