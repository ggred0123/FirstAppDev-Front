// ImageListResponse.kt
package com.example.mydev.model

data class ImageListResponse(
    val images: List<ImageData>
)

// ImageData.kt
data class ImageData(
    val id: Int,
    val url: String,
    val instagramIds: List<String>,
    val createdAt: String
)

// ImageUploadRequest.kt
data class ImageUploadRequest(
    val url: String,
    val instagramIds: List<String>,
    val createdAt: String
)

// ImageUploadResponse.kt
data class ImageUploadResponse(
    val id: Int,
    val url: String,
    val instagramIds: List<String>,
    val createdAt: String
)
