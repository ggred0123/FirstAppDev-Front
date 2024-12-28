package com.example.mydev.api

import com.example.mydev.model.ImageResponse
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.model.UploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CloudApiService {
    @GET("images")
    suspend fun getImages(): Response<ImageResponse>

    @POST("images/upload")
    suspend fun uploadImage(@Body imageUploadRequest: ImageUploadRequest): Response<UploadResponse>
}
