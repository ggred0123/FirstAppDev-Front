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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydev.adapter.ImageAdapter
import com.example.mydev.adapter.ItemMoveCallback
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.viewmodel.ImagesViewModel
import com.example.mydev.viewmodel.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

class ImagesFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var fabAddImage: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private val imageAdapter by lazy {
        ImageAdapter(
            context = requireContext(),
            items = mutableListOf()
        )
    }
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_images, container, false)

        // View 초기화
        fabAddImage = view.findViewById(R.id.fabAddImage)
        recyclerView = view.findViewById(R.id.recyclerViewImages)

        // RecyclerView 설정
        setupRecyclerView()

        // ItemTouchHelper 연결
        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(imageAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // 서버에서 이미지 목록 가져오기
        fetchImagesFromServer()

        // FloatingActionButton 클릭 리스너 설정
        fabAddImage.setOnClickListener {
            openGallery()
        }

        return view
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun setupRecyclerView() {
        // 이미지 클릭 이벤트 추가
        imageAdapter.setOnItemClickListener { imageId ->
            val dialog = ImageDetailDialogFragment.newInstance(imageId)
            dialog.show(childFragmentManager, "ImageDetailDialog")
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3열 그리드
        recyclerView.adapter = imageAdapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val dialog = ImageUploadDialogFragment.newInstance(selectedImageUri!!)
                // 업로드 성공 리스너 수정
                dialog.setOnUploadSuccessListener {
                    // 서버에서 새 데이터를 가져오고
                    fetchImagesFromServer()
                    // 다른 Fragment들에게 알림
                    lifecycleScope.launch {
                        sharedViewModel.notifyImageUpdated()
                    }
                }
                dialog.show(childFragmentManager, "ImageUploadDialog")
            }
        }
    }

    private fun fetchImagesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.imageApi.getImages()
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