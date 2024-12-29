package com.example.mydev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
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
    private var items: MutableList<ImageData> = mutableListOf()

) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // ViewHolder 정의
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
    }

    // 아이템 레이아웃 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    // 뷰에 데이터 바인딩
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = items[position]

        // Glide를 사용하여 이미지 로드
        Glide.with(context)
            .load(imageData.url)
            .into(holder.imgItem)

        holder.imgItem.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 드래그 시작
                    v.startDragAndDrop(null, View.DragShadowBuilder(v), v, 0)
                    true
                }
                else -> false
            }
        }
    }

    // 아이템 수 반환
    override fun getItemCount(): Int = items.size

    // 데이터 업데이트 메서드
    fun updateData(newItems: List<ImageData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    // 드래그 앤 드롭: 아이템 이동 처리
    fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val movedItem = items.removeAt(fromPosition)
        items.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

}

