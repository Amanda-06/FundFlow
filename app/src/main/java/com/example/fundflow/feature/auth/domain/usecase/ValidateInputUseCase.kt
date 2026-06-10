package com.example.fundflow.feature.auth.domain.usecase

import javax.inject.Inject

/**
 * Kumpulan fungsi validasi input form auth.
 * Return null berarti valid; return String berarti pesan error.
 */
class ValidateInputUseCase @Inject constructor() {

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email tidak boleh kosong"
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email.trim())) return "Format email tidak valid"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "Kata sandi tidak boleh kosong"
        if (password.length < 6) return "Kata sandi minimal 6 karakter"
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isBlank()) return "Konfirmasi kata sandi tidak boleh kosong"
        if (password != confirm) return "Kata sandi tidak cocok"
        return null
    }

    fun validateNamaLengkap(nama: String): String? {
        if (nama.isBlank()) return "Nama lengkap tidak boleh kosong"
        if (nama.trim().length < 2) return "Nama terlalu pendek"
        return null
    }

    fun validateUsername(username: String): String? {
        if (username.isBlank()) return "Username tidak boleh kosong"
        if (username.trim().length < 3) return "Username minimal 3 karakter"
        val usernameRegex = Regex("^[a-zA-Z0-9_]+$")
        if (!usernameRegex.matches(username.trim())) return "Username hanya boleh huruf, angka, dan underscore"
        return null
    }

    fun validateNotEmpty(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName tidak boleh kosong"
        return null
    }
}