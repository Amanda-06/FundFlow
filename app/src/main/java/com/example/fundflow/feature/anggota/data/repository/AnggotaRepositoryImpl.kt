// ============================================================
// feature/anggota/data/repository/AnggotaRepositoryImpl.kt
// ============================================================
package com.example.fundflow.feature.anggota.data.repository

import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.anggota.data.mapper.toDomain
import com.example.fundflow.feature.anggota.data.mapper.toEntity
import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnggotaRepositoryImpl @Inject constructor(
    private val dao: AnggotaDao
) : AnggotaRepository {

    override fun getAll(): Flow<List<Anggota>> =
        dao.getAllAnggota().map { list -> list.map { it.toDomain() } }

    override suspend fun add(anggota: Anggota): Long =
        dao.insertAnggota(anggota.toEntity())

    override suspend fun update(anggota: Anggota) =
        dao.updateAnggota(anggota.toEntity())

    override suspend fun delete(id: Int) =
        dao.deleteById(id)

    override suspend fun deleteSelected(ids: List<Int>) =
        dao.deleteByIds(ids)

    override suspend fun getById(id: Int): Anggota? =
        dao.getAnggotaById(id)?.toDomain()
}

