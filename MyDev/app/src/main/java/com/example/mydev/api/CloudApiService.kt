package com.example.mydev.api

import com.example.mydev.model.ImageResponse
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudApiService {
    @GET("images")
    suspend fun getImages(): Response<ImageResponse>

    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("createdAt") createdAt: RequestBody
    ): Response<UploadResponse>
}
