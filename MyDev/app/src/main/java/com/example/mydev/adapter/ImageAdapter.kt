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

        // 만약 '터치해서 바로 드래그'를 원하면, 아래처럼 setOnTouchListener를 사용할 수도 있습니다.
        // (롱 프레스로 드래그가 가능하다면 이 부분은 생략해도 OK)
        /*
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
        */
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
