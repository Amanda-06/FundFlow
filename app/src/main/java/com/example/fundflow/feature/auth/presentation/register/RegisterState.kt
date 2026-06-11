package com.example.fundflow.feature.auth.presentation.register

data class RegisterState(
    // Step 1 — data akun
    val namaLengkap: String           = "",
    val email: String                 = "",
    val username: String              = "",
    val password: String              = "",
    val confirmPassword: String       = "",

    // Step 1 errors
    val namaLengkapError: String?     = null,
    val emailError: String?           = null,
    val usernameError: String?        = null,
    val passwordError: String?        = null,
    val confirmPasswordError: String? = null,

    // Step 2 — setup organisasi
    val namaOrganisasi: String        = "",
    val periodeMulai: String          = "",    // format: "yyyy-MM" contoh: "2026-01"
    val periodeSelesai: String        = "",

    // Step 2 errors
    val namaOrganisasiError: String?  = null,
    val periodeMulaiError: String?    = null,
    val periodeSelesaiError: String?  = null,

    // Status umum
    val isLoading: Boolean            = false,
    val isSuccess: Boolean            = false,
    val errorMessage: String?         = null
)
 