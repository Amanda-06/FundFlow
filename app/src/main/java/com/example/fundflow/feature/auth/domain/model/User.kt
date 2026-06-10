package com.example.fundflow.feature.auth.domain.model

data class User(
    val userId: String,
    val namaLengkap: String,
    val username: String,
    val email: String,
    val namaOrganisasi: String = "",
    val createdAt: Long = System.currentTimeMillis()
)