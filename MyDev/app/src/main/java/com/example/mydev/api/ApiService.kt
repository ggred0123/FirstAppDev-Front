package com.example.mydev.api

import com.example.mydev.model.User
import com.example.mydev.model.UserCreate
import com.example.mydev.model.UserResponse
import com.example.mydev.model.UserUpdate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("users")
    suspend fun getUsers(): UserResponse

    @POST("users")
    suspend fun createUser(@Body user: UserCreate): User

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body userUpdate: UserUpdate
    ): User
}