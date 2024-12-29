package com.example.mydev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mydev.R
import com.example.mydev.databinding.ItemImageBinding
import com.example.mydev.model.ImageData

class ImageAdapter(
    private val context: Context,
    private var items: List<ImageData> = emptyList()
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ImageData = items[position]
    override fun getItemId(position: Int): Long = items[position].id.toLong()

    fun updateData(newItems: List<ImageData>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
            holder = ViewHolder(view.findViewById(R.id.imgItem))
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val imageData = getItem(position)

        // Glide or Picasso 사용 권장
        Glide.with(context)
            .load(imageData.url)
            .into(holder.imgItem)

        return view
    }

    private class ViewHolder(val imgItem: ImageView)
}
