// ImagesViewModel.kt
package com.example.mydev.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.model.ImageData
import com.example.mydev.model.ImageListResponse
import com.example.mydev.model.ImageUploadRequest
import com.example.mydev.model.ImageUploadResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ImagesViewModel : ViewModel() {

    val imageListLiveData = MutableLiveData<List<ImageData>>()    // GET 결과
    val uploadResultLiveData = MutableLiveData<ImageUploadResponse?>() // POST 결과

    // 1) 이미지 목록 불러오기
    fun fetchImages() {
        viewModelScope.launch {
            try {
                val response: Response<ImageListResponse> = RetrofitInstance.imageApi.getImages()
                if (response.isSuccessful) {
                    imageListLiveData.value = response.body()?.images ?: emptyList()
                } else {
                    // 실패 처리
                    imageListLiveData.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageListLiveData.value = emptyList()
            }
        }
    }

    // 2) 이미지 업로드
    fun uploadImage(request: ImageUploadRequest) {
        viewModelScope.launch {
            try {
                val response: Response<ImageUploadResponse> = RetrofitInstance.imageApi.uploadImage(request)
                if (response.isSuccessful) {
                    uploadResultLiveData.value = response.body()
                } else {
                    uploadResultLiveData.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uploadResultLiveData.value = null
            }
        }
    }
}
