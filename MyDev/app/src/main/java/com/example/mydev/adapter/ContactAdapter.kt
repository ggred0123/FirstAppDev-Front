package com.example.mydev.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydev.R
import com.example.mydev.model.User

class ContactAdapter(
    private val context: Context,
    var users: MutableList<User> = mutableListOf(),
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = view.findViewById(R.id.phoneTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        // 터치 애니메이션 설정
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val liftUpAnimation = AnimationUtils.loadAnimation(context, R.anim.item_lift_up)
                    v.startAnimation(liftUpAnimation)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val liftDownAnimation = AnimationUtils.loadAnimation(context, R.anim.item_lift_down)
                    v.startAnimation(liftDownAnimation)
                }
            }
            false
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.nameTextView.text = user.userName
        holder.phoneTextView.text = user.phoneNumber

        // Set default profile image
        holder.profileImageView.setImageResource(R.drawable.ic_add)



        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun removeUser(position: Int) {
        if (position >= 0 && position < users.size) {
            val user = users[position]
            users.removeAt(position)
            notifyItemRemoved(position)
            onDeleteClick(user)
        }
    }

    fun getUser(position: Int): User {
        return users[position]
    }
}