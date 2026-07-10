package com.example.myapplication.feature.dashboard

data class Order(
    val id: String,
    val customer: String = "",
    val service: String,
    val weight: Double,
    val price: Int,
    val status: String,
    val date: String,
    val paid: Boolean,
    val orderId: Long = -1L // raw numeric id, needed for API calls like advance-status
)

data class NotificationItem(
    val id: Int,
    val text: String,
    val time: String,
    val read: Boolean
)

// ─── Backend network models (FR-004, FR-005, FR-011) ─────────

data class NewOrderRequest(
    val userId: Long,
    val pickupAddress: String,
    val scheduledDate: String, // "YYYY-MM-DD"
    val timeSlot: String,
    val pickupNotes: String?,
    val serviceType: String,   // "WASH_FOLD" | "DRY_CLEAN" | "FOLD_ONLY"
    val weightKg: Double,
    val specialInstructions: String?
)

data class OrderResponse(
    val orderId: Long,
    val pickupId: Long,
    val pickupAddress: String,
    val scheduledDate: String,
    val timeSlot: String,
    val serviceType: String,
    val weightKg: Double,
    val totalPrice: Double,
    val status: String,
    val specialInstructions: String?,
    val paid: Boolean,
    val createdAt: String
)

// FR-007: staff-facing order queue response
data class StaffOrderResponse(
    val orderId: Long,
    val orderCode: String,
    val customerName: String,
    val pickupAddress: String,
    val scheduledDate: String,
    val timeSlot: String,
    val serviceType: String,
    val weightKg: Double,
    val totalPrice: Double,
    val status: String,
    val specialInstructions: String?,
    val paid: Boolean,
    val createdAt: String
)

// Maps a backend service type / status enum to the same display labels used on web
val SERVICE_TYPE_LABELS = mapOf(
    "WASH_FOLD" to "Wash & Fold",
    "DRY_CLEAN" to "Dry Clean",
    "FOLD_ONLY" to "Fold Only"
)

val STATUS_LABELS = mapOf(
    "PENDING" to "Pending",
    "PICKED_UP" to "Picked Up",
    "PROCESSING" to "Processing",
    "READY" to "Ready",
    "DELIVERED" to "Delivered"
)

fun OrderResponse.toUiOrder(): Order = Order(
    id = "ORD-${1000 + orderId}",
    service = SERVICE_TYPE_LABELS[serviceType] ?: serviceType,
    weight = weightKg,
    price = totalPrice.toInt(),
    status = STATUS_LABELS[status] ?: status,
    date = createdAt.substringBefore("T"),
    paid = paid,
    orderId = orderId
)

fun StaffOrderResponse.toStaffUiOrder(): Order = Order(
    orderId = orderId,
    id = orderCode,
    customer = customerName,
    service = SERVICE_TYPE_LABELS[serviceType] ?: serviceType,
    weight = weightKg,
    price = totalPrice.toInt(),
    status = STATUS_LABELS[status] ?: status,
    date = createdAt.substringBefore("T"),
    paid = paid
)
