package com.example.myapplication.feature.auth

import com.example.myapplication.api.NetworkClient
import retrofit2.Response

object AuthRepository {
    private val authService: AuthApiService by lazy {
        NetworkClient.retrofit.create(AuthApiService::class.java)
    }

    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return authService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        return authService.register(request)
    }
}
