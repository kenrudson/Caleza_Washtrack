package com.example.myapplication.feature.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val fullName: String,
    val email: String,
    val role: String
)

data class ErrorResponse(
    val message: String
)
