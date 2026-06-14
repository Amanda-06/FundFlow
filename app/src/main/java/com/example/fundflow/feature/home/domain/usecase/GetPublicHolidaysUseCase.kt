package com.example.fundflow.feature.home.domain.usecase

import com.example.fundflow.core.util.Resource
import com.example.fundflow.feature.home.domain.model.Holiday
import com.example.fundflow.feature.home.domain.repository.HomeRepository
import javax.inject.Inject
import java.util.Calendar

class GetPublicHolidaysUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        year: Int = Calendar.getInstance().get(Calendar.YEAR)
    ): Resource<List<Holiday>> = repository.getPublicHolidays(year)
}