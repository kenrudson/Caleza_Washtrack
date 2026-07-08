package com.example.myapplication.features.dashboard

import com.example.myapplication.api.AuthApiService

class DashboardRepository(private val apiService: AuthApiService) {
    suspend fun fetchOrdersForUser(userId: Long) = apiService.getOrdersForUser(userId)
}
