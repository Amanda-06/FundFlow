package com.example.fundflow.feature.profile.data.mapper

import com.example.fundflow.feature.auth.data.model.UserEntity
import com.example.fundflow.feature.profile.domain.model.Profile

fun UserEntity.toProfile(): Profile = Profile(
    userId         = userId,
    namaLengkap    = namaLengkap,
    username       = username,
    email          = email,
    namaOrganisasi = namaOrganisasi
)

fun Profile.toEntity(createdAt: Long): UserEntity = UserEntity(
    userId         = userId,
    namaLengkap    = namaLengkap,
    username       = username,
    email          = email,
    namaOrganisasi = namaOrganisasi,
    createdAt      = createdAt
)

/** Map Profile → field map untuk disimpan ke Firestore (merge update) */
fun Profile.toFirestoreMap(): Map<String, Any> = mapOf(
    "user_id"         to userId,
    "nama_lengkap"    to namaLengkap,
    "username"        to username,
    "email"           to email,
    "nama_organisasi" to namaOrganisasi
)