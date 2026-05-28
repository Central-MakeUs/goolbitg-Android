package com.project.data.remote.response.analysis

import com.google.gson.annotations.SerializedName
import com.project.domain.model.analysis.AnalysisReportModel
import com.project.domain.model.analysis.AnalysisSummary
import com.project.domain.model.analysis.BuyOrNotAnalysis
import com.project.domain.model.analysis.CategoryAnalysis
import com.project.domain.model.analysis.CategoryScore
import com.project.domain.model.analysis.CompletionAnalysis
import com.project.domain.model.analysis.IndvGroupAnalysis

data class AnalysisReportRes(
    @SerializedName("summary") val summary: SummaryRes,
    @SerializedName("completionAnalysis") val completionAnalysis: CompletionAnalysisRes,
    @SerializedName("categoryAnalysis") val categoryAnalysis: CategoryAnalysisRes,
    @SerializedName("indvGroupAnalysis") val indvGroupAnalysis: IndvGroupAnalysisRes,
    @SerializedName("buyOrNotAnalysis") val buyOrNotAnalysis: BuyOrNotAnalysisRes
) {
    fun toDomainModel() = AnalysisReportModel(
        summary = summary.toDomainModel(),
        completionAnalysis = completionAnalysis.toDomainModel(),
        categoryAnalysis = categoryAnalysis.toDomainModel(),
        indvGroupAnalysis = indvGroupAnalysis.toDomainModel(),
        buyOrNotAnalysis = buyOrNotAnalysis.toDomainModel()
    )
}

data class SummaryRes(
    @SerializedName("username") val username: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("percantage") val percentage: Int,
    @SerializedName("spendingType") val spendingType: String
) {
    fun toDomainModel() = AnalysisSummary(
        username = username,
        imageUrl = imageUrl,
        percentage = percentage,
        spendingType = spendingType
    )
}

data class CompletionAnalysisRes(
    @SerializedName("message") val message: String,
    @SerializedName("prev") val prev: Int,
    @SerializedName("current") val current: Int,
    @SerializedName("recommandation") val recommendation: Int
) {
    fun toDomainModel() = CompletionAnalysis(
        message = message,
        prev = prev,
        current = current,
        recommendation = recommendation
    )
}

data class CategoryAnalysisRes(
    @SerializedName("message") val message: String,
    @SerializedName("scores") val scores: List<CategoryScoreRes>
) {
    fun toDomainModel() = CategoryAnalysis(
        message = message,
        scores = scores.map { it.toDomainModel() }
    )
}

data class CategoryScoreRes(
    @SerializedName("catName") val catName: String,
    @SerializedName("total") val total: Int,
    @SerializedName("success") val success: Int
) {
    fun toDomainModel() = CategoryScore(catName = catName, total = total, success = success)
}

data class IndvGroupAnalysisRes(
    @SerializedName("message") val message: String,
    @SerializedName("indvScore") val indvScore: Float,
    @SerializedName("groupScore") val groupScore: Float
) {
    fun toDomainModel() = IndvGroupAnalysis(message = message, indvScore = indvScore, groupScore = groupScore)
}

data class BuyOrNotAnalysisRes(
    @SerializedName("message") val message: String,
    @SerializedName("buyScore") val buyScore: Int,
    @SerializedName("notScore") val notScore: Int
) {
    fun toDomainModel() = BuyOrNotAnalysis(message = message, buyScore = buyScore, notScore = notScore)
}
