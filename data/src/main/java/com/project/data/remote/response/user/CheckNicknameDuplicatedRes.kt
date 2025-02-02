package com.project.data.remote.response.user

import com.google.gson.annotations.SerializedName
import com.project.domain.model.user.CheckNicknameModel

data class CheckNicknameDuplicatedRes(
    @SerializedName("duplicated") val duplicated: Boolean
){
    fun toDomainModel() = CheckNicknameModel(
        duplicated = duplicated
    )
}
