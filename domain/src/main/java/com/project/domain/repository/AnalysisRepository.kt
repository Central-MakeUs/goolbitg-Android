package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.analysis.AnalysisReportModel
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    suspend fun fetchAnalysisReport(): Flow<DataState<AnalysisReportModel>>
}
