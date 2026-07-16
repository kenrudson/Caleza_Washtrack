package com.example.myapplication.feature.profile

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApiService {
    @GET("profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: Long): Response<ProfileResponse>

    @PUT("profile/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Long,
        @Body request: ProfileRequest
    ): Response<ProfileResponse>

    @PUT("profile/{userId}/change-password")
    suspend fun changePassword(
        @Path("userId") userId: Long,
        @Body request: ProfilePasswordRequest
    ): Response<Unit>
}
