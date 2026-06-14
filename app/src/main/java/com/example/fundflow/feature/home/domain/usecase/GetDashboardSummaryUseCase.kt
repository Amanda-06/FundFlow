package com.example.fundflow.feature.home.domain.usecase

import com.example.fundflow.feature.home.domain.model.DashboardSummary
import com.example.fundflow.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<DashboardSummary> = repository.getDashboardSummary()
}
