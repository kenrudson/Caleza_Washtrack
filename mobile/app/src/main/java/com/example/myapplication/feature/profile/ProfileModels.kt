package com.example.myapplication.feature.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("role") val role: String,
    @SerializedName("createdAt") val createdAt: String?
)

data class ProfileRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String
)

data class ProfilePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
