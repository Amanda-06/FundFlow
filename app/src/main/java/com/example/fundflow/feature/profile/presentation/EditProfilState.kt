package com.example.fundflow.feature.profile.presentation

data class EditProfilState(
    val userId: String          = "",
    val namaLengkap: String      = "",
    val username: String         = "",
    val email: String             = "",
    val namaOrganisasi: String    = "",

    val namaLengkapError: String? = null,
    val usernameError: String?    = null,

    val isLoading: Boolean        = true,
    val isSaving: Boolean         = false,
    val isSuccess: Boolean        = false,
    val errorMessage: String?     = null
)