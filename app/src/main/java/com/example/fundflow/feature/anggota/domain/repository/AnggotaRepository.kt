package com.example.fundflow.feature.anggota.domain.repository

import com.example.fundflow.feature.anggota.domain.model.Anggota
import kotlinx.coroutines.flow.Flow

interface AnggotaRepository {
    fun getAll(): Flow<List<Anggota>>
    suspend fun add(anggota: Anggota): Long
    suspend fun update(anggota: Anggota)
    suspend fun delete(id: Int)
    suspend fun deleteSelected(ids: List<Int>)
    suspend fun getById(id: Int): Anggota?
}
