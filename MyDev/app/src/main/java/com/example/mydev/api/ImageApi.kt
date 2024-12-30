package com.example.mydev.api

import com.example.mydev.model.ImageData
import com.example.mydev.model.ImageListResponse
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.model.ImageUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageApi {

    @POST("images")
    suspend fun uploadImageData(
        @Body request: ImageUploadRequest
    ): Response<ImageUploadResponse>

    @GET("images")
    suspend fun getImages(): Response<ImageListResponse>

    @GET("images/instagram/{instagramId}")
    suspend fun getInstagramData(
        @Path("instagramId") instagramId: String
    ): Response<ImageListResponse>

    @GET("images/detail/{imageId}")
    suspend fun getImageDetail(
        @Path("imageId") imageId: String
    ): Response<ImageData>

    @POST("images/upload")
    suspend fun uploadImage(
        @Body request: ImageUploadRequest
    ): Response<ImageUploadResponse>


}
