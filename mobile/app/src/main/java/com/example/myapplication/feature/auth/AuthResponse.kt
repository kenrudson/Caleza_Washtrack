package com.example.myapplication.feature.auth

data class AuthResponse(
    val token: String,
    val userId: Long,
    val fullName: String,
    val email: String,
    val role: String
)
