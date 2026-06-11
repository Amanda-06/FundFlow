package com.example.fundflow.feature.pengeluaran.data.repository

import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.feature.pengeluaran.data.mapper.toDomain
import com.example.fundflow.feature.pengeluaran.data.mapper.toDomainList
import com.example.fundflow.feature.pengeluaran.data.mapper.toEntity
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PengeluaranRepositoryImpl @Inject constructor(
    private val dao: PengeluaranDao
) : PengeluaranRepository {
    override fun getAll(): Flow<List<Pengeluaran>>           = dao.getAll().map { it.toDomainList() }
    override fun search(query: String): Flow<List<Pengeluaran>> = dao.search(query).map { it.toDomainList() }
    override suspend fun add(p: Pengeluaran): Long           = dao.insert(p.toEntity())
    override suspend fun update(p: Pengeluaran)              = dao.update(p.toEntity())
    override suspend fun delete(id: Int)                     = dao.deleteById(id)
    override suspend fun deleteSelected(ids: List<Int>)      = dao.deleteByIds(ids)
    override suspend fun getById(id: Int): Pengeluaran?      = dao.getById(id)?.toDomain()
    override suspend fun getByDateRange(s: String, e: String): List<Pengeluaran> =
        dao.getByDateRange(s, e).toDomainList()
}