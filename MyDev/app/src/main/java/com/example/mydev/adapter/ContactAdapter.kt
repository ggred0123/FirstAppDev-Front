package com.example.mydev.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // 데이터 바인딩
        val user = getItem(position)
        holder.nameTextView.text = user.userName
        holder.phoneTextView.text = user.phoneNumber

        // 기본 프로필 이미지를 설정하거나 사용자 이미지 추가
        Log.d("ContactAdapter", "Setting image resource: ${user.profileImageRes}")
        holder.profileImageView.setImageResource(user.profileImageRes)
// 기본 아이콘
        // 예를 들어, Glide를 사용해 사용자 프로필 이미지 로드 가능:
        // Glide.with(view.context).load(user.profileImageUrl).into(holder.profileImageView)

        return view
    }

    class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = view.findViewById(R.id.phoneTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView) // 프로필 이미지 추가
    }
}
