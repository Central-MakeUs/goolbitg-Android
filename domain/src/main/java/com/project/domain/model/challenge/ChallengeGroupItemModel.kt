package com.project.domain.model.challenge

data class ChallengeGroupItemModel(
    val id: Int,
    val ownerId: String,
    val title: String,
    val reward: Int,
    val hashtags: List<String>,
    val maxSize: Int,
    val peopleCount: Int,
    val isHidden: Boolean,
    val password: String?,
    val avgAchieveRatio: Int,
    val maxAchieveDays: Int
) {
    fun getTagString(): String =
        hashtags.joinToString(" ") { "#$it" }
}
