package com.example.mydev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import com.example.mydev.model.ImageUploadRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

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

        imageAdapter = ImageAdapter(requireContext(), imageUrls)
        gridView.adapter = imageAdapter

        fabAddImage.setOnClickListener {
            openGallery()
        }

        fetchImages() // 클라우드에서 이미지 가져오기

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*" // 이미지 파일만 표시
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                uploadImageToCloud(selectedImageUri)
            }
        }
    }

    private fun fetchImages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.cloudApi.getImages()
                if (response.isSuccessful) {
                    val images = response.body()?.images ?: emptyList<String>()
                    imageAdapter.updateImages(images)
                } else {
                    Toast.makeText(context, "Failed to fetch images", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToCloud(imageUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val imageBytes = inputStream?.readBytes()
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val uploadRequest = ImageUploadRequest(
            file = "uploaded_image.jpg",
            instagramId = "",
            createdAt = base64Image
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.cloudApi.uploadImage(uploadRequest)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    fetchImages() // 업로드 후 목록 갱신
                } else {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
}



