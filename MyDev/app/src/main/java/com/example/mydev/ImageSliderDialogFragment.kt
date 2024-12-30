package com.example.mydev

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mydev.adapter.ImageSliderAdapter
import com.example.mydev.model.ImageData

class ImageSliderDialogFragment : DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageSliderAdapter
    private var images: List<ImageData> = emptyList()

    companion object {
        private const val ARG_IMAGES = "images"

        fun newInstance(images: List<ImageData>): ImageSliderDialogFragment {
            val fragment = ImageSliderDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_IMAGES, ArrayList(images))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomAlertDialog)
        arguments?.let {
            images = it.getSerializable(ARG_IMAGES) as? ArrayList<ImageData> ?: mutableListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_image_slider, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // 이미지 설정 (Glide 또는 다른 방법으로 설정)
        if (images.isNotEmpty()) {
            Glide.with(requireContext())
                .load(images[0].url) // 첫 번째 이미지를 표시
                .into(imageView)
        }

        return view
    }

}
