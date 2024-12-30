package com.example.mydev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mydev.R
import com.example.mydev.model.ImageData

class ImageAdapter(
    private val context: Context,
    private var items: MutableList<ImageData> = mutableListOf()
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(), ItemTouchHelperAdapter {

    private var onItemClickListener: ((Int) -> Unit)? = null

    // ViewHolder 정의
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(items[position].id)
                }
            }
        }

    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    // 아이템 레이아웃 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = items[position]

        Glide.with(context)
            .load(imageData.url)
            .into(holder.imgItem)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(imageData.id)
        }
    }


    // 아이템 수 반환
    override fun getItemCount(): Int = items.size

    // --------------- ItemTouchHelperAdapter 구현부 ---------------
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        // 리스트 내 아이템의 위치를 바꿔준다
        val movedItem = items.removeAt(fromPosition)
        items.add(toPosition, movedItem)
        // 어댑터에게 아이템 이동이 일어났음을 알린다
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        // 스와이프로 삭제를 구현하고 싶으면 이 부분에서 처리
        // items.removeAt(position)
        // notifyItemRemoved(position)
    }
    // ----------------------------------------------------------

    // 데이터 업데이트 메서드
    fun updateData(newItems: List<ImageData>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }
}
