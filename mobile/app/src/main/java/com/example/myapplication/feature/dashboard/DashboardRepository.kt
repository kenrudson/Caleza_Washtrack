package com.example.myapplication.feature.dashboard

import com.example.myapplication.api.NetworkClient

object DashboardRepository {

    private val apiService: DashboardApiService by lazy {
        NetworkClient.retrofit.create(DashboardApiService::class.java)
    }

    private val notifications = listOf(
        NotificationItem(1, "Your order ORD-1042 is now being processed.", "2 hours ago", false),
        NotificationItem(2, "Order ORD-1038 is ready for pickup!", "5 hours ago", false),
        NotificationItem(3, "Your order ORD-1035 has been delivered.", "2 days ago", true),
        NotificationItem(4, "Payment received for ORD-1035. Thank you!", "2 days ago", true)
    )

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

    suspend fun loadNotifications(): List<NotificationItem> {
        return notifications
    }

    // FR-004 + FR-005: creates the pickup request and order together, same as the web app.
    // Returns the created order on success, or an error message on failure.
    suspend fun submitNewOrder(request: NewOrderRequest): Result<Order> {
        return try {
            val response = apiService.createOrder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUiOrder())
            } else {
                val message = response.errorBody()?.string() ?: "Could not create order"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to server. Please try again."))
        }
    }

    // FR-007: advances an order to its next status (BR-003 sequence enforced server-side)
    suspend fun advanceOrderStatus(orderId: Long): Result<Order> {
        return try {
            val response = apiService.advanceOrderStatus(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toStaffUiOrder())
            } else {
                val message = response.errorBody()?.string() ?: "Could not update order status"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to server. Please try again."))
        }
    }

    // NOTE: payment recording does not yet have a backend endpoint — kept as a
    // known follow-up item, same as the web app's current state.
}

