package com.example.myapplication.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderApiService {
    @GET("orders/my/{userId}")
    suspend fun getOrdersForUser(@Path("userId") userId: Long): Response<List<OrderResponse>>
}

data class OrderResponse(
    val orderId: Long,
    val fullName: String,
    val serviceType: String,
    val weightKg: Double,
    val totalPrice: Double,
    val status: String,
    val createdAt: String,
    val paid: Boolean
)
