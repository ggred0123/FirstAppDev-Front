package com.example.mydev.repository

import com.example.mydev.model.AWSS3Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface AWSS3Repository {
    suspend fun getPreSignedUrl(accessKey: String, secretKey: String, fileName: String): Response<AWSS3Response>
    suspend fun uploadImageToS3(preSignedUrl: String, file: RequestBody): Response<Void>
}
