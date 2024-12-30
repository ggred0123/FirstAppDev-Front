// ImageListResponse.kt
package com.example.mydev.model

import android.os.Parcelable
import java.io.Serializable

data class ImageListResponse(
    val images: ArrayList<ImageData>
): Serializable

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

data class Album(
    val instagramId: String,
    val images: List<ImageData>
)
