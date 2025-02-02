package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserCheckListReq(
    @SerializedName("check1") val check1: Boolean,
    @SerializedName("check2") val check2: Boolean,
    @SerializedName("check3") val check3: Boolean,
    @SerializedName("check4") val check4: Boolean,
    @SerializedName("check5") val check5: Boolean,
    @SerializedName("check6") val check6: Boolean,
)
