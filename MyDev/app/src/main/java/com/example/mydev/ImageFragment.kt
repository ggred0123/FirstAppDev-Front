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
import android.app.DatePickerDialog
import java.util.Calendar

class ImagesFragment : Fragment() {

    private lateinit var fabAddImage: FloatingActionButton
    private lateinit var gridView: GridView
    private val imageUrls = mutableListOf<String>() // 클라우드에서 가져온 이미지 URL 리스트
    private lateinit var imageAdapter: ImageAdapter
    private var selectedDate: String = ""

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
            showDatePicker()
        }

        fetchImages() // 클라우드에서 이미지 가져오기

        return view
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // ISO 8601 형식으로 날짜 포맷팅
            selectedDate = String.format("%04d-%02d-%02dT00:00:00.000Z",
                selectedYear,
                selectedMonth + 1,
                selectedDay)
            openGallery() // 날짜 선택 후 갤러리 열기
        }, year, month, day).show()
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
                uploadImageToCloud(selectedImageUri, selectedDate)  // selectedDate 추가
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

    private fun uploadImageToCloud(imageUri: Uri, userInputDate: String) {  // 파라미터 수정
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val imageBytes = inputStream?.readBytes()
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val uploadRequest = ImageUploadRequest(
            url = base64Image,
            instagramIds = listOf(),    // 빈 리스트 추가
            createdAt = userInputDate   // 날짜 추가
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.cloudApi.uploadImage(uploadRequest)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    fetchImages()
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



