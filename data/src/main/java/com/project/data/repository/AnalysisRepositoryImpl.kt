package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.AnalysisDataSource
import com.project.domain.model.DataState
import com.project.domain.model.analysis.AnalysisReportModel
import com.project.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val dataSource: AnalysisDataSource
) : AnalysisRepository {
    override suspend fun fetchAnalysisReport(): Flow<DataState<AnalysisReportModel>> =
        NetworkUtils.handleApi(
            execute = { dataSource.fetchAnalysisReport() },
            mapper = { it?.toDomainModel() }
        )
}
