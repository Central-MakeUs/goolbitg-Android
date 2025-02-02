package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserAgreementReq(
    @SerializedName("agreement1") val agreement1: Boolean,
    @SerializedName("agreement2") val agreement2: Boolean,
    @SerializedName("agreement3") val agreement3: Boolean,
    @SerializedName("agreement4") val agreement4: Boolean,
)
