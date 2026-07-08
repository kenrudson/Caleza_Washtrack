package com.example.myapplication.features.login

import com.example.myapplication.api.AuthApiService
import com.example.myapplication.api.LoginRequest

class LoginRepository(private val apiService: AuthApiService) {
    suspend fun login(email: String, password: String) =
        apiService.login(LoginRequest(email, password))
}
