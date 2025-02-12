package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserHabitReq(
    @SerializedName("avgIncomePerMonth") val avgIncomePerMonth: Int,
    @SerializedName("avgSavingPerMonth") val avgSavingPerMonth: Int
)
