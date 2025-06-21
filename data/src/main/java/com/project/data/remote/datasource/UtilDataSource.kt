package com.project.data.remote.datasource

import com.project.data.remote.response.util.UploadImgRes
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UtilDataSource {
    @Multipart
    @POST("/api/v1/images")
    suspend fun uploadImg(
        @Part image: MultipartBody.Part
    ): Response<UploadImgRes>

}
