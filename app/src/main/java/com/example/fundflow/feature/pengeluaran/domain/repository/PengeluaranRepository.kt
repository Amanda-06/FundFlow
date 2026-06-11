package com.example.fundflow.feature.pengeluaran.domain.repository

import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import kotlinx.coroutines.flow.Flow

interface PengeluaranRepository {
    fun getAll(): Flow<List<Pengeluaran>>
    fun search(query: String): Flow<List<Pengeluaran>>
    suspend fun add(p: Pengeluaran): Long
    suspend fun update(p: Pengeluaran)
    suspend fun delete(id: Int)
    suspend fun deleteSelected(ids: List<Int>)
    suspend fun getById(id: Int): Pengeluaran?
    suspend fun getByDateRange(startDate: String, endDate: String): List<Pengeluaran>
}