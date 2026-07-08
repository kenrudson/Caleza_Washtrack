package com.example.myapplication.features.dashboard

import com.example.myapplication.api.OrderApiService

class DashboardRepository(private val apiService: OrderApiService) {
    suspend fun fetchOrdersForUser(userId: Long) = apiService.getOrdersForUser(userId)
}
