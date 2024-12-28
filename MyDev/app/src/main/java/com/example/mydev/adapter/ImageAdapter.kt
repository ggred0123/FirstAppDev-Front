package com.example.mydev.adapter
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageAdapter(private val context: Context, private val images: List<Uri>) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView = convertView as? ImageView
            ?: ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

        Glide.with(context)
            .load(images[position])
            .into(imageView)

        return imageView
    }
}
