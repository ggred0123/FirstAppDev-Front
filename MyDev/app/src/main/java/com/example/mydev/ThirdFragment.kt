package com.example.mydev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydev.adapter.AlbumAdapter
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.model.Album
import com.example.mydev.model.ImageData
import com.example.mydev.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.activityViewModels


class ThirdTabFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이미지 업데이트 이벤트 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.imageUpdateTrigger.collect {
                    // 이미지가 업데이트되면 새로운 데이터를 가져옴
                    fetchAlbumsFromServer()
                }
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_tab, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAlbums)
        setupRecyclerView()

        // 서버에서 앨범 리스트 가져오기
        fetchAlbumsFromServer()

        return view
    }

    private fun setupRecyclerView() {
        // AlbumAdapter 초기화
        albumAdapter = AlbumAdapter(
            context = requireContext(),
            albums = listOf(), // 초기 리스트는 비워둠
            onAlbumClick = { album ->
                // 다이얼로그 띄우기
                val dialog = ImageSliderDialogFragment.newInstance(album.images)
                dialog.show(childFragmentManager, "ImageSliderDialog")
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // 세로 레이아웃
        recyclerView.adapter = albumAdapter
    }


    private fun fetchAlbumsFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.imageApi.getImages() // 서버에서 이미지 리스트 가져오기
                if (response.isSuccessful) {
                    val images = response.body()?.images ?: mutableListOf()

                    // 그룹화된 앨범 리스트 생성
                    val albums = groupImagesByInstagramId(images)

                    withContext(Dispatchers.Main) {
                        albumAdapter.updateAlbums(albums) // 데이터를 어댑터에 업데이트
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch albums", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun groupImagesByInstagramId(images: List<ImageData>): List<Album> {
        return images.filterNot { it.instagramIds.isNullOrEmpty() }
            .groupBy { it.instagramIds.firstOrNull() ?: "Unknown" }
            .map { (instagramId, imageList) ->
                Album(
                    instagramId = instagramId,
                    images = imageList
                )
            }
    }

}
