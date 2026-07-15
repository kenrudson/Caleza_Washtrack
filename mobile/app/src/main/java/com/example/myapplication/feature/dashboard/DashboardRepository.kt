package com.example.myapplication.feature.dashboard

import com.example.myapplication.api.NetworkClient
import com.google.gson.Gson

object DashboardRepository {

    private val apiService: DashboardApiService by lazy {
        NetworkClient.retrofit.create(DashboardApiService::class.java)
    }

    private data class ErrorBody(val message: String?)

    // Backend errors come back as JSON, e.g. {"message": "..."} — this pulls out
    // just the message text instead of showing the raw JSON to the user.
    private fun parseErrorMessage(rawBody: String?, fallback: String): String {
        if (rawBody.isNullOrBlank()) return fallback
        return try {
            Gson().fromJson(rawBody, ErrorBody::class.java)?.message ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

    // FR-007: staff order queue now comes from the real backend, same as the customer side.
    suspend fun loadOrders(isStaff: Boolean, userId: Long = -1L): List<Order> {
        return try {
            if (isStaff) {
                val response = apiService.getAllOrdersForStaff()
                if (response.isSuccessful) {
                    response.body()?.map { it.toStaffUiOrder() } ?: emptyList()
                } else {
                    emptyList()
                }
            } else {
                val response = apiService.getMyOrders(userId)
                if (response.isSuccessful) {
                    response.body()?.map { it.toUiOrder() } ?: emptyList()
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // FR-010: real notifications, works identically for CUSTOMER and STAFF accounts
    suspend fun loadNotifications(userId: Long): List<NotificationItem> {
        return try {
            val response = apiService.getMyNotifications(userId)
            if (response.isSuccessful) {
                response.body()?.map { it.toUiNotification() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun markNotificationsRead(userId: Long) {
        try {
            apiService.markNotificationsRead(userId)
        } catch (e: Exception) {
            // Non-critical — if this fails, notifications simply stay marked unread
            // until the next successful attempt.
        }
    }

    // FR-004 + FR-005: creates the pickup request and order together, same as the web app.
    // Returns the created order on success, or an error message on failure.
    suspend fun submitNewOrder(request: NewOrderRequest): Result<Order> {
        return try {
            val response = apiService.createOrder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUiOrder())
            } else {
                val message = parseErrorMessage(response.errorBody()?.string(), "Could not create order")
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to server. Please try again."))
        }
    }

    // FR-007: advances an order to its next status (BR-003 sequence, plus the pickup-date
    // check, both enforced server-side)
    suspend fun advanceOrderStatus(orderId: Long): Result<Order> {
        return try {
            val response = apiService.advanceOrderStatus(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toStaffUiOrder())
            } else {
                val message = parseErrorMessage(response.errorBody()?.string(), "Could not update order status")
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to server. Please try again."))
        }
    }

    // Interim lightweight payment marking, reusing the existing Order.paid flag.
    // Does not yet record payment type (on-delivery vs on-pickup) or create a
    // dedicated Payment record as described in the approved ERD — planned separately.
    suspend fun markOrderAsPaid(orderId: Long): Result<Order> {
        return try {
            val response = apiService.markOrderAsPaid(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toStaffUiOrder())
            } else {
                val message = parseErrorMessage(response.errorBody()?.string(), "Could not record payment")
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to server. Please try again."))
        }
    }
}
