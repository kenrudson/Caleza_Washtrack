package com.example.myapplication.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.OrderResponse
import com.example.myapplication.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardViewModel(
    private val repository: DashboardRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _orders = MutableStateFlow<List<DashboardOrder>>(emptyList())
    val orders: StateFlow<List<DashboardOrder>> = _orders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadOrders() {
        val userId = sessionManager.getUserId()
        if (userId < 0) return

        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.fetchOrdersForUser(userId)
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = "Unable to load dashboard orders."
            } finally {
                _loading.value = false
            }
        }
    }

    private fun handleResponse(response: Response<List<OrderResponse>>) {
        if (response.isSuccessful && response.body() != null) {
            _orders.value = response.body()!!.map { it.toDashboardOrder() }
            _error.value = ""
        } else {
            _error.value = "Failed to load orders."
        }
    }
}

private fun OrderResponse.toDashboardOrder(): DashboardOrder {
    return DashboardOrder(
        id = "ORD-${1000 + orderId}",
        customer = fullName,
        service = serviceType,
        weight = weightKg,
        price = totalPrice,
        status = status,
        date = createdAt.split("T").firstOrNull().orEmpty(),
        paid = paid
    )
}

data class DashboardOrder(
    val id: String,
    val customer: String,
    val service: String,
    val weight: Double,
    val price: Double,
    val status: String,
    val date: String,
    val paid: Boolean
)

data class OrderResponse(
    val orderId: Long,
    val fullName: String,
    val serviceType: String,
    val weightKg: Double,
    val totalPrice: Double,
    val status: String,
    val createdAt: String,
    val paid: Boolean
)
