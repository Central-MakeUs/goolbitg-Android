package com.project.data.remote.datasource

import com.project.data.remote.response.analysis.AnalysisReportRes
import retrofit2.Response
import retrofit2.http.GET

interface AnalysisDataSource {
    @GET("/api/v1/analysis/report")
    suspend fun fetchAnalysisReport(): Response<AnalysisReportRes>
}
