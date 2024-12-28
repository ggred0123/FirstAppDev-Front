package com.example.mydev.model

import com.example.mydev.R

data class User(
    val id: Int,
    val userName: String,
    val profileImageRes: Int = R.drawable.ic_person,
    val email: String,
    val birthday: String,
    val phoneNumber: String,
    val instagramId: String,
    val createdAt: String
)
data class UserCreate(
    val userName: String,
    val email: String,
    val birthday: String,
    val phoneNumber: String,
    val instagramId: String
)

data class UserResponse(
    val users: List<User>
)

data class UserUpdate(
    val userName: String? = null,
    val email: String? = null,
    val birthday: String? = null,
    val phoneNumber: String? = null,
    val instagramId: String? = null
)