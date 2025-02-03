package com.project.data.remote.response.user

import com.google.gson.annotations.SerializedName
import com.project.domain.model.user.RegisterStatus
import com.project.domain.model.user.RegisterStatusModel

data class CheckRegisterStatusRes(
    @SerializedName("status") val status: Int,
    @SerializedName("requiredInfoCompleted") val requiredInfoCompleted: Boolean
){
    fun toDomainModel(): RegisterStatusModel{
        val convertedStatus = RegisterStatus.entries.find { it.code == status } ?: RegisterStatus.FirstOnboarding
        return RegisterStatusModel(
            status = convertedStatus,
            requiredInfoCompleted = requiredInfoCompleted
        )
    }
}
