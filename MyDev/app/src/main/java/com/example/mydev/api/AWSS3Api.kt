package com.example.mydev.api

import com.example.mydev.model.AWSS3Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface AWSS3Api {


    @GET("aws/s3")
    suspend fun getPreSignedUrl(
        @Query("fileName") fileName: String
    ): Response<AWSS3Response>

    @PUT
    suspend fun uploadImageToS3(
        @Url preSignedUrl: String,
        @Body file: RequestBody
    ): Response<Void>  // S3는 보통 200(또는 204)로 응답
}
