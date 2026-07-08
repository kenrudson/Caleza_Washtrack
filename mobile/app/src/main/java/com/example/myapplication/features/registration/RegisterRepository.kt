package com.example.myapplication.features.registration

import com.example.myapplication.api.AuthApiService
import com.example.myapplication.api.RegisterRequest

class RegisterRepository(private val apiService: AuthApiService) {
    suspend fun register(request: RegisterRequest) = apiService.register(request)
}
