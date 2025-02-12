package com.project.data.remote.response.user

import com.google.gson.annotations.SerializedName
import com.project.domain.model.user.SpendingTypeModel
import com.project.domain.model.user.UserInfoModel

data class GetUserInfoRes(
    @SerializedName("id")
    val id: String,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("check1")
    val check1: Boolean?,
    @SerializedName("check2")
    val check2: Boolean?,
    @SerializedName("check3")
    val check3: Boolean?,
    @SerializedName("check4")
    val check4: Boolean?,
    @SerializedName("check5")
    val check5: Boolean?,
    @SerializedName("check6")
    val check6: Boolean?,
    @SerializedName("avgIncomePerMonth")
    val avgIncomePerMonth: Int?,
    @SerializedName("avgSpendingPerMonth")
    val avgSpendingPerMonth: Int?,
    @SerializedName("primeUseDay")
    val primeUseDay: String?,
    @SerializedName("primeUseTime")
    val primeUseTime: String?,
    @SerializedName("spendingType")
    val spendingType: SpendingTypeRes?,
    @SerializedName("spendingHabitScore")
    val spendingHabitScore: Int?,
    @SerializedName("challengeCount")
    val challengeCount: Int,
    @SerializedName("postCount")
    val postCount: Int,
    @SerializedName("achievementGuage")
    val achievementGuage: Int,
) {
    fun toDomainModel() = UserInfoModel(
        id = id,
        nickname = nickname,
        birthday = birthday,
        gender = gender,
        check1 = check1,
        check2 = check2,
        check3 = check3,
        check4 = check4,
        check5 = check5,
        check6 = check6,
        avgIncomePerMonth = avgIncomePerMonth,
        avgSpendingPerMonth = avgSpendingPerMonth,
        primeUseDay = primeUseDay,
        primeUseTime = primeUseTime,
        spendingType = spendingType?.toDomainModel(),
        spendingHabitScore = spendingHabitScore,
        challengeCount = challengeCount,
        postCount = postCount,
        achievementGuage = achievementGuage,
    )
}

data class SpendingTypeRes(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("profileUrl") val profileUrl: String?,
    @SerializedName("goal") val goal: Int?,
    @SerializedName("peopleCount") val peopleCount: Int?,
) {
    fun toDomainModel() = SpendingTypeModel(
        id = id,
        title = title,
        imgUrl = imageUrl,
        profileUrl = profileUrl,
        goal = goal,
        peopleCount = peopleCount,
    )
}
