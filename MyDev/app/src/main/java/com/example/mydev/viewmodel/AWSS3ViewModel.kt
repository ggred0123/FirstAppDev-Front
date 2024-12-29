package com.example.mydev.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydev.model.AWSS3Response
import com.example.mydev.repository.AWSS3Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject



class AWSS3ViewModel(
    private val awsS3Repository: AWSS3Repository
) : ViewModel() {
    private val _preSignedUrl = MutableLiveData<AWSS3Response?>()
    val preSignedUrl: LiveData<AWSS3Response?> = _preSignedUrl

    private val _uploadImageResponse = MutableLiveData<Int?>()
    val uploadImageResponse: LiveData<Int?> = _uploadImageResponse

    fun getPreSignedUrl(
        accessKey: String,
        secretKey: String,
        fileName: String
    ) {
        viewModelScope.launch {
            try {
                _preSignedUrl.value = awsS3Repository.getPreSignedUrl(
                    accessKey = accessKey,
                    secretKey = secretKey,
                    fileName = fileName
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadImageToS3(preSignedUrl: String, file: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _uploadImageResponse.value = awsS3Repository.uploadImageToS3(preSignedUrl, file)
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadImageResponse.value = null
            }
        }
    }
}