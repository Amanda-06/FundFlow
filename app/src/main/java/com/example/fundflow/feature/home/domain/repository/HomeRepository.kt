package com.example.fundflow.feature.home.domain.repository

import com.example.fundflow.core.util.Resource
import com.example.fundflow.feature.home.domain.model.DashboardSummary
import com.example.fundflow.feature.home.domain.model.Holiday
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getDashboardSummary(): Flow<DashboardSummary>
    suspend fun getPublicHolidays(year: Int): Resource<List<Holiday>>
}
