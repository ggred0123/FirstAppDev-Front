package com.example.mydev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mydev.R
import com.example.mydev.model.User

class ContactAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val users = mutableListOf<User>()

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

        val user = getItem(position)
        holder.nameTextView.text = user.userName
        holder.phoneTextView.text = user.phoneNumber

        return view
    }

    private class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = view.findViewById(R.id.phoneTextView)
    }
}