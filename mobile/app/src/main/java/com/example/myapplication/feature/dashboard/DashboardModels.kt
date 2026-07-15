package com.example.myapplication.feature.dashboard

data class Order(
    val id: String,
    val customer: String = "",
    val address: String = "",
    val service: String,
    val weight: Double,
    val price: Int,
    val status: String,
    val date: String,
    val paid: Boolean,
    val orderId: Long = -1L // raw numeric id, needed for API calls like advance-status
)

data class NotificationItem(
    val id: Long,
    val text: String,
    val time: String,
    val read: Boolean
)

// FR-010: real notifications, works identically for CUSTOMER and STAFF accounts
data class NotificationResponse(
    val notedId: Long,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val sentAt: String
)

fun NotificationResponse.toUiNotification(): NotificationItem = NotificationItem(
    id = notedId,
    text = message,
    time = formatTimeAgo(sentAt),
    read = isRead
)

// Turns an ISO timestamp like "2026-07-12T14:30:00" into "2 hours ago", "3 days ago", etc.
fun formatTimeAgo(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        // Parse "yyyy-MM-dd'T'HH:mm:ss" manually (no external date library dependency)
        val datePart = isoString.substringBefore("T")
        val timePart = isoString.substringAfter("T").take(8) // "HH:mm:ss"
        val (year, month, day) = datePart.split("-").map { it.toInt() }
        val (hour, minute, second) = timePart.split(":").map { it.toIntOrNull() ?: 0 }

        val cal = java.util.Calendar.getInstance()
        cal.set(year, month - 1, day, hour, minute, second)
        val thenMillis = cal.timeInMillis
        val nowMillis = System.currentTimeMillis()
        val seconds = (nowMillis - thenMillis) / 1000

        when {
            seconds < 60 -> "Just now"
            seconds < 3600 -> "${seconds / 60} minute${if (seconds / 60 == 1L) "" else "s"} ago"
            seconds < 86400 -> "${seconds / 3600} hour${if (seconds / 3600 == 1L) "" else "s"} ago"
            seconds < 604800 -> "${seconds / 86400} day${if (seconds / 86400 == 1L) "" else "s"} ago"
            else -> "$month/$day/$year"
        }
    } catch (e: Exception) {
        ""
    }
}

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
    address = pickupAddress,
    service = SERVICE_TYPE_LABELS[serviceType] ?: serviceType,
    weight = weightKg,
    price = totalPrice.toInt(),
    status = STATUS_LABELS[status] ?: status,
    date = createdAt.substringBefore("T"),
    paid = paid
)
