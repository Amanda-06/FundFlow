package com.example.fundflow.feature.pemasukan.data.repository

import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pemasukan.data.mapper.toDomain
import com.example.fundflow.feature.pemasukan.data.mapper.toDomainList
import com.example.fundflow.feature.pemasukan.data.mapper.toEntity
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PemasukanRepositoryImpl @Inject constructor(
    private val dao: PemasukanDao
) : PemasukanRepository {

    override fun getAll(): Flow<List<Pemasukan>> =
        dao.getAll().map { it.toDomainList() }

    override fun search(query: String): Flow<List<Pemasukan>> =
        dao.search(query).map { it.toDomainList() }

    override suspend fun add(pemasukan: Pemasukan): Long =
        dao.insert(pemasukan.toEntity())

    override suspend fun update(pemasukan: Pemasukan) =
        dao.update(pemasukan.toEntity())

    override suspend fun delete(id: Int) =
        dao.deleteById(id)

    override suspend fun deleteSelected(ids: List<Int>) =
        dao.deleteByIds(ids)

    override suspend fun getById(id: Int): Pemasukan? =
        dao.getById(id)?.toDomain()

    override suspend fun getByDateRange(
        startDate: String,
        endDate: String
    ): List<Pemasukan> = dao.getByDateRange(startDate, endDate).toDomainList()
}