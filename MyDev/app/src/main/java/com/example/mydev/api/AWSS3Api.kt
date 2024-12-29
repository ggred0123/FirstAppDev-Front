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

    // 1) Presigned URL 발급용
    @GET("aws/s3")
    suspend fun getPreSignedUrl(
        @Header("AccessKey") accessKey: String,
        @Header("SecretKey") secretKey: String,
        @Query("fileName") fileName: String
    ): Response<AWSS3Response>

    // 2) 실제 S3 업로드 (PUT)
    @PUT
    suspend fun uploadImageToS3(
        @Url preSignedUrl: String,
        @Body file: RequestBody
    ): Response<Void>  // S3는 보통 200(또는 204)로 응답
}
