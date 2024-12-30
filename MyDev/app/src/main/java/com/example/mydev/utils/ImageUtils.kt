package com.example.mydev.utils

import com.example.mydev.model.Album
import com.example.mydev.model.ImageData

object ImageUtils {
    fun groupImagesByInstagramId(images: List<ImageData>): List<Album> {
        return images.groupBy { it.instagramIds.firstOrNull() ?: "Unknown" }
            .map { (instagramId, imageList) ->
                Album(
                    instagramId = instagramId, // Instagram ID를 이름으로 대체
                    images = imageList
                )
            }
    }
}
