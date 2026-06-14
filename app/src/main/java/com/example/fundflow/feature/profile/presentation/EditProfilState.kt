package com.example.fundflow.feature.profile.presentation

data class EditProfilState(
    val userId: String            = "",
    val namaLengkap: String       = "",
    val username: String          = "",
    val email: String             = "",
    val namaOrganisasi: String    = "",

    // ── Ubah Password ────────────────────────────────────────
    val passwordSaatIni: String          = "",
    val passwordBaru: String             = "",
    val konfirmasiPassword: String       = "",

    val passwordSaatIniError: String?    = null,
    val passwordBaruError: String?       = null,
    val konfirmasiPasswordError: String? = null,

    val passwordSaatIniVisible: Boolean  = false,
    val passwordBaruVisible: Boolean     = false,
    val konfirmasiPasswordVisible: Boolean = false,

    val namaLengkapError: String? = null,
    val usernameError: String?    = null,

    val isLoading: Boolean        = true,
    val isSaving: Boolean         = false,
    val isSuccess: Boolean        = false,
    val errorMessage: String?     = null
)