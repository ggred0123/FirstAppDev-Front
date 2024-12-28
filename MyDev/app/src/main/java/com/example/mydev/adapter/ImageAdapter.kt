package com.example.mydev.adapter
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mydev.R

class ImageAdapter(private val context: Context, private var images: List<String>) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView = convertView as? ImageView
            ?: ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(300, 300) // 이미지 크기
                scaleType = ImageView.ScaleType.CENTER_CROP // 이미지 크롭 방식
            }

        Glide.with(context)
            .load(images[position]) // URL 리스트에서 이미지 로드
            .placeholder(R.drawable.ic_placeholder) // 로딩 중 기본 이미지
            .error(R.drawable.ic_error) // 오류 시 기본 이미지
            .into(imageView)

        return imageView
    }

    fun updateImages(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }
}

