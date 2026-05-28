package com.project.domain.usecase.analysis

import com.project.domain.repository.AnalysisRepository
import javax.inject.Inject

class FetchAnalysisReportUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    suspend operator fun invoke() = repository.fetchAnalysisReport()
}
