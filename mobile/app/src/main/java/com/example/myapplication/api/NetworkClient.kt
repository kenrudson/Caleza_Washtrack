package com.example.myapplication.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    // 10.0.2.2 is the special IP address that routes to localhost on the development machine from the Android emulator.
    // If testing on a physical device, replace this with your machine's local IP address (e.g., 192.168.x.x).
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

<<<<<<< HEAD
    private val retrofit by lazy {
=======
    val retrofit: Retrofit by lazy {
>>>>>>> ccf7463243dfe01ba11fe0586113bc7eecfb4ea5
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
<<<<<<< HEAD
    }

    val apiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val orderApiService: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
=======
>>>>>>> ccf7463243dfe01ba11fe0586113bc7eecfb4ea5
    }
}
