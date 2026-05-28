package com.project.domain.model.analysis

data class AnalysisReportModel(
    val summary: AnalysisSummary,
    val completionAnalysis: CompletionAnalysis,
    val categoryAnalysis: CategoryAnalysis,
    val indvGroupAnalysis: IndvGroupAnalysis,
    val buyOrNotAnalysis: BuyOrNotAnalysis
)

data class AnalysisSummary(
    val username: String,
    val imageUrl: String,
    val percentage: Int,
    val spendingType: String
)

data class CompletionAnalysis(
    val message: String,
    val prev: Int,
    val current: Int,
    val recommendation: Int
)

data class CategoryAnalysis(
    val message: String,
    val scores: List<CategoryScore>
)

data class CategoryScore(
    val catName: String,
    val total: Int,
    val success: Int
)

data class IndvGroupAnalysis(
    val message: String,
    val indvScore: Float,
    val groupScore: Float
)

data class BuyOrNotAnalysis(
    val message: String,
    val buyScore: Int,
    val notScore: Int
)
