package com.example.mydev.model

import com.google.gson.annotations.SerializedName

data class AWSS3Response(
    @SerializedName("preSignedUrl")
    val presignedUrl: String,
    // 필요하다면 더 추가 가능 (예: fileName)
)



data class UserImage(
    val instagramId: String
)
