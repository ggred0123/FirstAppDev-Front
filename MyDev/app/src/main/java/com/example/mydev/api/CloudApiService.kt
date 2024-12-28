package com.example.mydev.api

import com.example.mydev.model.ImageResponse
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.model.UploadResponse
import com.example.mydev.model.User
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CloudApiService {
    @GET("/cloud/images")
    suspend fun getImages(): Response<ImageResponse>

    @POST("/cloud/upload")
    suspend fun uploadImage(@Body image: ImageUploadRequest): Response<UploadResponse>
}
