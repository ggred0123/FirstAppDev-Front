package com.example.mydev.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydev.repository.AWSS3Repository
import com.example.mydev.model.AWSS3Response
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AWSS3ViewModel(private val repository: AWSS3Repository) : ViewModel() {

    val preSignedUrl: MutableLiveData<AWSS3Response?> = MutableLiveData()
    val uploadImageResponse: MutableLiveData<Int?> = MutableLiveData()

    fun getPreSignedUrl(accessKey: String, secretKey: String, fileName: String) {
        viewModelScope.launch {
            try {
                val response = repository.getPreSignedUrl(accessKey, secretKey, fileName)
                if (response.isSuccessful) {
                    preSignedUrl.value = response.body()
                } else {
                    // 실패 시 처리
                    preSignedUrl.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                preSignedUrl.value = null
            }
        }
    }

    fun uploadImageToS3(preSignedUrl: String, file: RequestBody) {
        viewModelScope.launch {
            try {
                val response = repository.uploadImageToS3(preSignedUrl, file)
                if (response.isSuccessful) {
                    // S3는 일반적으로 200 또는 204 No Content 반환
                    uploadImageResponse.value = response.code() // 200, 204 등
                } else {
                    uploadImageResponse.value = response.code() // 오류 코드
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uploadImageResponse.value = null
            }
        }
    }
}
