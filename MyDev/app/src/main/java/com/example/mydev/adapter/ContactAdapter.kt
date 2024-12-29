package com.example.mydev.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mydev.R
import com.example.mydev.model.User

class ContactAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val users = mutableListOf<User>()

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = users.size
    override fun getItem(position: Int): User = users[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치 시작 시 위로 떠오르는 애니메이션 적용
                    val liftUpAnimation = AnimationUtils.loadAnimation(parent?.context ?: v.context, R.anim.item_lift_up)
                    v.startAnimation(liftUpAnimation)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 터치 종료 시 원래 위치로 돌아오는 애니메이션 적용
                    val liftDownAnimation = AnimationUtils.loadAnimation(parent?.context ?: v.context, R.anim.item_lift_down)
                    v.startAnimation(liftDownAnimation)
                }
            }
            false
        }




        // 데이터 바인딩
        val user = getItem(position)
        holder.nameTextView.text = user.userName
        holder.phoneTextView.text = user.phoneNumber

        Log.d("ContactAdapter", "Setting image resource: ${user.profileImageRes}")
        holder.profileImageView.setImageResource(user.profileImageRes)

        return view
    }

    class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = view.findViewById(R.id.phoneTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView) // 프로필 이미지 추가
    }
}
