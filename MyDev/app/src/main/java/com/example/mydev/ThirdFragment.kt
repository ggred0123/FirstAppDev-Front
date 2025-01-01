package com.example.mydev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

class ThirdTabFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var searchEditText: EditText
    private var allAlbums: List<Album> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_tab, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAlbums)
        searchEditText = view.findViewById(R.id.searchEditText)

        setupRecyclerView()
        setupSearchView()
        fetchAlbumsFromServer()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.imageUpdateTrigger.collect {
                    fetchAlbumsFromServer()
                }
            }
        }
    }

    private fun setupSearchView() {
        searchEditText.addTextChangedListener { text ->
            filterAlbums(text?.toString() ?: "")
        }
    }

    private fun filterAlbums(query: String) {
        if (query.isEmpty()) {
            albumAdapter.updateAlbums(allAlbums)
            return
        }

        val filteredAlbums = allAlbums.filter { album ->
            album.instagramId.contains(query, ignoreCase = true)
        }
        albumAdapter.updateAlbums(filteredAlbums)
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter(
            context = requireContext(),
            albums = listOf(),
            onAlbumClick = { album ->
                val dialog = ImageSliderDialogFragment.newInstance(album.images)
                dialog.show(childFragmentManager, "ImageSliderDialog")
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = albumAdapter
    }

    private fun fetchAlbumsFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.imageApi.getImages()
                if (response.isSuccessful) {
                    val images = response.body()?.images ?: mutableListOf()
                    allAlbums = groupImagesByInstagramId(images)

                    withContext(Dispatchers.Main) {
                        // 현재 검색어를 기준으로 필터링
                        val currentQuery = searchEditText.text?.toString() ?: ""
                        if (currentQuery.isEmpty()) {
                            albumAdapter.updateAlbums(allAlbums)
                        } else {
                            filterAlbums(currentQuery)
                        }
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