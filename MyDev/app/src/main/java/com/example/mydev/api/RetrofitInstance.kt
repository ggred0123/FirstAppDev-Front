package com.example.mydev.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitInstance {
    private const val CONTACTS_BASE_URL = "https://week1-service-755589139480.asia-northeast3.run.app/"
    private const val CLOUD_BASE_URL = "https://your-cloud-service-url.com/" // 클라우드 API URL

    // 연락처 API Retrofit 인스턴스
    private val contactsRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CONTACTS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val contactsApi: ApiService by lazy {
        contactsRetrofit.create(ApiService::class.java)
    }

    // 클라우드 API Retrofit 인스턴스
    private val cloudRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cloudApi: CloudApiService by lazy {
        cloudRetrofit.create(CloudApiService::class.java)
    }
}
