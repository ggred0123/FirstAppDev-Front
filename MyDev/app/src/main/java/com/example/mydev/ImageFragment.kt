package com.example.mydev

import android.content.Context
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mydev.adapter.ImageAdapter
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.repository.AWSS3RepositoryImpl
import com.example.mydev.viewmodel.AWSS3ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ImagesFragment : Fragment() {

    private lateinit var fabAddImage: FloatingActionButton
    private lateinit var gridView: GridView
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var awsS3ViewModel: AWSS3ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewModel 수동 초기화
        val awsS3Repository = AWSS3RepositoryImpl(RetrofitInstance.awsS3Api)
        awsS3ViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AWSS3ViewModel(awsS3Repository) as T
            }
        })[AWSS3ViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_images, container, false)

        fabAddImage = view.findViewById(R.id.fabAddImage)
        gridView = view.findViewById(R.id.gridViewImages)

        imageAdapter = ImageAdapter(requireContext(), selectedImages)
        gridView.adapter = imageAdapter

        fabAddImage.setOnClickListener {
            openGallery()
        }

        setupObservers()

        return view
    }

    private fun setupObservers() {
        // PreSigned URL 응답 관찰
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            response?.let {
                selectedImages.lastOrNull()?.let { uri ->
                    val file = getFileFromUri(uri)
                    val multipartBody = createMultipartBody(file)
                    awsS3ViewModel.uploadImageToS3(response.presignedUrl, multipartBody)
                }
            }
        }

        // 업로드 결과 관찰
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                200, 201 -> {
                    Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                }
                null -> {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Error: $response", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                selectedImages.add(selectedImageUri)
                imageAdapter.notifyDataSetChanged()

                // 이미지 선택 후 S3 업로드 처리
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val filePath = "$PARENT_FOLDER_PATH$fileName"

                // PreSigned URL 요청
                awsS3ViewModel.getPreSignedUrl(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY, filePath)
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val file = File(context?.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun createMultipartBody(file: File): MultipartBody.Part {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }



    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val PARENT_FOLDER_PATH = "images/"

    }
}


