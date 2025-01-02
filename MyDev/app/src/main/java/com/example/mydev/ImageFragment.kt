package com.example.mydev

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

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

        val tvUploadedImages: TextView = view.findViewById(R.id.tvUploadedImages)
        val fullText = "Uploaded Images"
        val spannableString = SpannableString(fullText)
        val purpleColor = ForegroundColorSpan(Color.parseColor("#BE44BE")) // 보라색 컬러코드
        spannableString.setSpan(purpleColor, 9, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvUploadedImages.text = spannableString

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedViewModel의 이미지 업데이트 이벤트 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.imageUpdateTrigger.collect {
                    // 이미지가 업데이트되면 새로운 데이터를 가져옴
                    fetchImagesFromServer()
                }
            }
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun setupRecyclerView() {
        imageAdapter.setOnItemClickListener { imageId ->
            val dialog = ImageDetailDialogFragment.newInstance(imageId)
            dialog.show(childFragmentManager, "ImageDetailDialog")
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = imageAdapter

            // 가로, 세로 간격 각각 설정
            val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.grid_horizontal_spacing) // 예: 8dp
            val verticalSpacing = resources.getDimensionPixelSize(R.dimen.grid_vertical_spacing)     // 예: 16dp

            if (itemDecorationCount == 0) {
                addItemDecoration(GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing))
            }
        }
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

    private class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val horizontalSpacing: Int,  // 가로 간격
        private val verticalSpacing: Int,    // 세로 간격
        private val includeEdge: Boolean = true
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
                outRect.right = (column + 1) * horizontalSpacing / spanCount
                outRect.top = verticalSpacing
                outRect.bottom = verticalSpacing
            } else {
                outRect.left = column * horizontalSpacing / spanCount
                outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
                if (position >= spanCount) {
                    outRect.top = verticalSpacing
                }
            }
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
}