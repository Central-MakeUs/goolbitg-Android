package com.project.presentation.analysis

import com.project.domain.model.analysis.AnalysisReportModel

data class AnalysisState(
    val isLoading: Boolean = false,
    val report: AnalysisReportModel? = null,
    val errorMessage: String? = null
)
