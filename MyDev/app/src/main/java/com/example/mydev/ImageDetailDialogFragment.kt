package com.example.mydev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.mydev.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDetailDialogFragment : DialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var tvInstagramId: TextView
    private lateinit var tvCreatedAt: TextView

    companion object {
        private const val ARG_IMAGE_ID = "imageId"

        fun newInstance(imageId: String): ImageDetailDialogFragment {
            val fragment = ImageDetailDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_ID, imageId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_image_detail, container, false)

        imageView = view.findViewById(R.id.imageView)
        tvInstagramId = view.findViewById(R.id.tvInstagramId)
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt)

        val imageId = arguments?.getString(ARG_IMAGE_ID) ?: return view

        fetchImageDetail(imageId)

        return view
    }

    private fun fetchImageDetail(imageId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.imageApi.getImageDetail(imageId)
                if (response.isSuccessful) {
                    val imageData = response.body()
                    withContext(Dispatchers.Main) {
                        imageData?.let {
                            Glide.with(requireContext())
                                .load(it.url)
                                .into(imageView)

                            tvInstagramId.text = "Instagram ID: ${it.instagramIds.joinToString(", ")}"
                            tvCreatedAt.text = "Created At: ${it.createdAt}"
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load image details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
