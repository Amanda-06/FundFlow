package com.example.fundflow.feature.profile.domain.model

data class Profile(
    val userId: String,
    val namaLengkap: String,
    val username: String,
    val email: String,
    val namaOrganisasi: String = ""
)