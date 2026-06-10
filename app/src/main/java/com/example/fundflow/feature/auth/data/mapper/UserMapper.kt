// ============================================================
// feature/auth/data/mapper/UserMapper.kt
// ============================================================
package com.example.fundflow.feature.auth.data.mapper

import com.example.fundflow.feature.auth.data.model.UserEntity
import com.example.fundflow.feature.auth.domain.model.User

fun UserEntity.toDomain(): User = User(
    userId         = userId,
    namaLengkap    = namaLengkap,
    username       = username,
    email          = email,
    namaOrganisasi = namaOrganisasi,
    createdAt      = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    userId         = userId,
    namaLengkap    = namaLengkap,
    username       = username,
    email          = email,
    namaOrganisasi = namaOrganisasi,
    createdAt      = createdAt
)

/** Konversi Map dari Firestore ke UserEntity */
fun Map<String, Any>.toUserEntity(): UserEntity = UserEntity(
    userId         = (this["user_id"] as? String).orEmpty(),
    namaLengkap    = (this["nama_lengkap"] as? String).orEmpty(),
    username       = (this["username"] as? String).orEmpty(),
    email          = (this["email"] as? String).orEmpty(),
    namaOrganisasi = (this["nama_organisasi"] as? String).orEmpty(),
    createdAt      = (this["created_at"] as? Long) ?: System.currentTimeMillis()
)