package com.example.myapplication.feature.dashboard

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DashboardApiService {
    @POST("orders/new")
    suspend fun createOrder(@Body request: NewOrderRequest): Response<OrderResponse>

    @GET("orders/my/{userId}")
    suspend fun getMyOrders(@Path("userId") userId: Long): Response<List<OrderResponse>>

    // FR-007: staff-facing order queue and status advancement
    @GET("staff/orders")
    suspend fun getAllOrdersForStaff(): Response<List<StaffOrderResponse>>

    @POST("staff/orders/{orderId}/advance-status")
    suspend fun advanceOrderStatus(@Path("orderId") orderId: Long): Response<StaffOrderResponse>
}
