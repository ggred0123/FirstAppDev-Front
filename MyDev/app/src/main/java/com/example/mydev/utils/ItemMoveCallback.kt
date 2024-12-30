package com.example.mydev.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveCallback(
    private val adapter: ItemTouchHelperAdapter
) : ItemTouchHelper.Callback() {

    // 어느 방향으로 드래그/스와이프를 허용할지 설정
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 드래그 플래그: 상, 하, 좌, 우 전부 허용 (GridLayout이라면 보통 4방향 가능)
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        // 스와이프 플래그: 여기서는 스와이프 기능은 사용하지 않는다고 가정
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    // onMove: 실제 드래그로 순서 변경될 때 호출
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 어댑터 쪽에 위치 변경 처리
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    // onSwiped: 스와이프 되었을 때 (여기선 사용 X)
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 스와이프 삭제를 구현하려면 adapter.onItemDismiss(...) 호출
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    // 롱 프레스 시 드래그가 가능하도록 허용
    override fun isLongPressDragEnabled(): Boolean = true

    // 스와이프를 사용하지 않거나 롱 클릭으로만 사용하고 싶으면 false
    override fun isItemViewSwipeEnabled(): Boolean = false
}
