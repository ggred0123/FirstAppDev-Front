package com.example.mydev.model

import com.example.mydev.R

data class User(
    val id: Int,
    val userName: String,
    val profileImageRes: Int = R.drawable.ic_person,
    val email: String,
    val birthday: String,
    val phoneNumber: String,
    val instagramId: String,
    val createdAt: String
)
data class UserCreate(
    val userName: String,
    val email: String,
    val birthday: String,
    val phoneNumber: String,
    val instagramId: String
)

data class UserResponse(
    val users: List<User>
)

data class UserUpdate(
    val userName: String? = null,
    val email: String? = null,
    val birthday: String? = null,
    val phoneNumber: String? = null,
    val instagramId: String? = null
)

data class ImageUploadRequest(
    val imageBase64: String, // Base64로 인코딩된 이미지 데이터
    val filename: String       // Base64로 인코딩된 이미지 데이터
)
data class ImageResponse(
    val images: List<String> // 서버가 반환하는 JSON 구조에 따라 수정
)

data class UploadResponse(
    val success: Boolean,
    val message: String // 서버가 반환하는 JSON 필드에 따라 수정
)



