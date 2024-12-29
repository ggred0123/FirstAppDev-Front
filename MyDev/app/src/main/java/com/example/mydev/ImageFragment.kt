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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydev.adapter.ImageAdapter
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.viewmodel.ImagesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesFragment : Fragment() {

    private lateinit var fabAddImage: FloatingActionButton
    private lateinit var gridView: GridView

    // 실제 서버 이미지 목록
    private val imageAdapter by lazy { ImageAdapter(requireContext()) }

    // 업로드(갤러리)에서 선택된 로컬 파일 Uri
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_images, container, false)

        fabAddImage = view.findViewById(R.id.fabAddImage)
        gridView = view.findViewById(R.id.gridViewImages)

        gridView.adapter = imageAdapter

        // 1) 진입 시 서버에서 이미지 목록 GET
        fetchImagesFromServer()

        // 2) FAB 클릭 → 갤러리 오픈
        fabAddImage.setOnClickListener {
            openGallery()
        }

        return view
    }

    // 갤러리 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    // 갤러리 선택 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // 이제 Dialog 열어서 Instagram ID/날짜 입력 후 업로드 진행
                val dialog = ImageUploadDialogFragment.newInstance(selectedImageUri!!)
                dialog.setOnUploadSuccessListener {
                    // 업로드 성공 후 → 새 목록 받아오기
                    fetchImagesFromServer()
                }
                dialog.show(childFragmentManager, "ImageUploadDialog")
            }
        }
    }

    // 서버에서 GET /images
    private fun fetchImagesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.imageApi.getImages() // suspend fun
                if (response.isSuccessful) {
                    val list = response.body()?.images ?: emptyList()
                    withContext(Dispatchers.Main) {
                        imageAdapter.updateData(list)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
}

