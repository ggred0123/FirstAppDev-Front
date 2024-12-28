package com.example.mydev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mydev.adapter.ImageAdapter
import com.example.mydev.api.RetrofitInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class ImagesFragment : Fragment() {

    private lateinit var fabAddImage: FloatingActionButton
    private lateinit var gridView: GridView
    private val imageUrls = mutableListOf<String>() // 클라우드에서 가져온 이미지 URL 리스트
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_images, container, false)

        fabAddImage = view.findViewById(R.id.fabAddImage)
        gridView = view.findViewById(R.id.gridViewImages)

        // 어댑터 초기화
        imageAdapter = ImageAdapter(requireContext(), imageUrls)
        gridView.adapter = imageAdapter

        // FloatingActionButton 클릭 리스너
        fabAddImage.setOnClickListener {
            fetchImages()
        }

        return view
    }

    // 클라우드에서 이미지 가져오기
    private fun fetchImages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.cloudApi.getImages()
                if (response.isSuccessful) {
                    val images = response.body()?.images
                    // images 리스트를 어댑터에 전달하여 UI 업데이트
                    imageAdapter.updateImages(images.map {
                    })
                } else {
                    Toast.makeText(context, "Failed to fetch images", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}


