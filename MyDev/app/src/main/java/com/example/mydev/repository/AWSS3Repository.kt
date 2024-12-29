package com.example.mydev.repository

import com.example.mydev.model.AWSS3Response
import okhttp3.MultipartBody

interface AWSS3Repository {
    suspend fun getPreSignedUrl(
        accessKey: String,
        secretKey: String,
        fileName: String
    ): AWSS3Response

    suspend fun uploadImageToS3(
        preSignedUrl: String,
        file: MultipartBody.Part,
    ): Int?
}