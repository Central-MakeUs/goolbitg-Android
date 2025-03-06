package com.project.data.remote.response.notice

import com.google.gson.annotations.SerializedName
import com.project.domain.model.notice.FetchNoticesItemModel
import com.project.domain.model.notice.FetchNoticesModel

data class FetchNoticesRes(
    @SerializedName("totalSize") val totalSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<FetchNoticesItemRes>,
) {
    fun toDomainModel() = FetchNoticesModel(
        totalSize = totalSize,
        totalPages = totalPages,
        size = size,
        page = page,
        items = items.map { it.toDomainModel() },
    )
}

data class FetchNoticesItemRes(
    @SerializedName("id") val id: Int,
    @SerializedName("receiverId") val receiverId: String,
    @SerializedName("message") val message: String,
    @SerializedName("publishDateTime") val publishDateTime: String,
    @SerializedName("type") val type: String,
    @SerializedName("read") val read: Boolean,
) {
    fun toDomainModel() = FetchNoticesItemModel(
        id = id,
        receiverId = receiverId,
        message = message,
        publishDateTime = publishDateTime,
        type = type,
        read = read,
    )
}
