package com.example.mydev.repository

import android.util.Log
import com.example.mydev.api.AWSS3Api
import com.example.mydev.model.AWSS3Response
import okhttp3.MultipartBody
import org.json.JSONObject
import javax.inject.Inject

class AWSS3RepositoryImpl(
    private val awsS3RetrofitClient: AWSS3Api
) : AWSS3Repository {
    override suspend fun getPreSignedUrl(
        accessKey: String,
        secretKey: String,
        fileName: String
    ): AWSS3Response {
        val response = awsS3RetrofitClient.getPreSignedUrl(accessKey, secretKey, fileName)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get presigned URL: ${response.code()}")
        }
    }

    override suspend fun uploadImageToS3(
        preSignedUrl: String,
        file: MultipartBody.Part
    ): Int? {
        return try {
            val response = awsS3RetrofitClient.uploadImageToS3(preSignedUrl, file)
            if (response.isSuccessful) {
                Log.d("AWSS3Repo-upload", "통신 성공 ${response.code()}")
                response.code()
            } else {
                val stringToJson = JSONObject(response.errorBody()?.string()!!)
                Log.e("AWSS3Repo-upload", "통신 실패 ${response.code()}\n$stringToJson")
                response.code()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}