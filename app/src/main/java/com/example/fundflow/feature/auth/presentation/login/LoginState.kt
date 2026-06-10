package com.example.fundflow.feature.auth.presentation.login

data class LoginState(
    val email: String              = "",
    val password: String           = "",
    val emailError: String?        = null,
    val passwordError: String?     = null,
    val isLoading: Boolean         = false,
    val isSuccess: Boolean         = false,
    val errorMessage: String?      = null
)