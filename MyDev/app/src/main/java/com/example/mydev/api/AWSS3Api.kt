package com.example.mydev.api

import com.example.mydev.model.AWSS3Response
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File

interface AWSS3Api {
    @GET("aws/s3")
    suspend fun getPreSignedUrl(
        @Header("AccessKey") accessKey: String,
        @Header("SecretKey") secretKey: String,
        @Query("fileName") fileName: String
    ): Response<AWSS3Response>

    @PUT
    suspend fun uploadImageToS3(
        @Url preSignedUrl: String,
        @Body file: MultipartBody.Part
    ): Response<Int>
}
