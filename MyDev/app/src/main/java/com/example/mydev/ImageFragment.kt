package com.example.mydev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mydev.adapter.ImageAdapter
import com.example.mydev.adapter.ItemMoveCallback
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.viewmodel.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import android.graphics.Rect

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
        initializeViews(view)
        setupRecyclerView()
        fetchImagesFromServer()
        return view
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewImages)
        fabAddImage = view.findViewById(R.id.fabAddImage)

        fabAddImage.setOnClickListener {
            openGallery()
        }
    }

    private fun setupRecyclerView() {
        // Grid Layout 설정
        recyclerView.layoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = 1
            }
        }

        // 아이템 간격 설정
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
                outRect.set(spacing, spacing, spacing, spacing)
            }
        })

        // 이미지 클릭 이벤트 설정
        imageAdapter.setOnItemClickListener { imageId ->
            val dialog = ImageDetailDialogFragment.newInstance(imageId)
            dialog.show(childFragmentManager, "ImageDetailDialog")
        }

        // 어댑터 연결
        recyclerView.adapter = imageAdapter

        // 드래그 앤 드롭 설정
        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(imageAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                val dialog = ImageUploadDialogFragment.newInstance(uri)
                dialog.setOnUploadSuccessListener {
                    fetchImagesFromServer()
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