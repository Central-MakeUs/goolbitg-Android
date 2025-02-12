package com.project.domain.model.user

data class UserInfoModel(
    val id: String,
    val nickname: String?,
    val birthday: String?,
    val gender: String?,
    val check1: Boolean?,
    val check2: Boolean?,
    val check3: Boolean?,
    val check4: Boolean?,
    val check5: Boolean?,
    val check6: Boolean?,
    val avgIncomePerMonth: Int?,
    val avgSpendingPerMonth: Int?,
    val primeUseDay: String?,
    val primeUseTime: String?,
    val spendingType: SpendingTypeModel?,
    val spendingHabitScore: Int?,
    val challengeCount: Int,
    val postCount: Int,
    val achievementGuage: Int,
)

data class SpendingTypeModel(
    val id: Int?,
    val title: String?,
    val imgUrl: String?,
    val profileUrl: String?,
    val goal: Int?,
    val peopleCount: Int?
)
