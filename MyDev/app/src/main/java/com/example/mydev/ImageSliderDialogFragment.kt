package com.example.mydev

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View

import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mydev.adapter.ImageSliderAdapter
import com.example.mydev.model.ImageData
import android.graphics.Color
import android.view.ViewGroup

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
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomAlertDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 다이얼로그 크기를 화면의 90%로 설정
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            setLayout(width, height)

            // 배경 설정 (선택적으로 조정 가능)
            setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            images = it.getSerializable(ARG_IMAGES) as? ArrayList<ImageData> ?: mutableListOf()
        }

        // 레이아웃 설정
        val view = inflater.inflate(R.layout.dialog_image_slider, container, false)

        // ViewPager2 설정
        viewPager = view.findViewById(R.id.viewPager)
        imageAdapter = ImageSliderAdapter(images)
        viewPager.adapter = imageAdapter

        return view
    }
}