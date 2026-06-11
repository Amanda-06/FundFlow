package com.example.fundflow.feature.pemasukan.domain.repository

import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import kotlinx.coroutines.flow.Flow

interface PemasukanRepository {
    fun getAll(): Flow<List<Pemasukan>>
    fun search(query: String): Flow<List<Pemasukan>>
    suspend fun add(pemasukan: Pemasukan): Long
    suspend fun update(pemasukan: Pemasukan)
    suspend fun delete(id: Int)
    suspend fun deleteSelected(ids: List<Int>)
    suspend fun getById(id: Int): Pemasukan?
    suspend fun getByDateRange(startDate: String, endDate: String): List<Pemasukan>
}