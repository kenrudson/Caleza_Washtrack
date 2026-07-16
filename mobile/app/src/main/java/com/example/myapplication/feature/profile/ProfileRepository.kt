package com.example.myapplication.feature.profile

import com.example.myapplication.api.NetworkClient
import com.google.gson.Gson

object ProfileRepository {
    private val apiService: ProfileApiService by lazy {
        NetworkClient.retrofit.create(ProfileApiService::class.java)
    }

    private data class ErrorBody(val message: String?)

    private fun parseErrorMessage(rawBody: String?, fallback: String): String {
        if (rawBody.isNullOrBlank()) return fallback
        return try {
            Gson().fromJson(rawBody, ErrorBody::class.java)?.message ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

    suspend fun getProfile(userId: Long): Result<ProfileResponse> {
        return try {
            val response = apiService.getProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), "Failed to get profile")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to server. Please try again."))
        }
    }

    suspend fun updateProfile(userId: Long, fullName: String, phone: String, address: String): Result<ProfileResponse> {
        return try {
            val request = ProfileRequest(fullName = fullName, phone = phone, address = address)
            val response = apiService.updateProfile(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), "Failed to update profile")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to server. Please try again."))
        }
    }

    suspend fun changePassword(userId: Long, currentPw: String, newPw: String): Result<Unit> {
        return try {
            val request = ProfilePasswordRequest(currentPassword = currentPw, newPassword = newPw)
            val response = apiService.changePassword(userId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), "Failed to change password")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to server. Please try again."))
        }
    }
}
