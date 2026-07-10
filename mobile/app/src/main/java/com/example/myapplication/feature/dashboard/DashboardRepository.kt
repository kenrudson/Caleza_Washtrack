package com.example.myapplication.feature.dashboard

object DashboardRepository {
    private val customerOrders = listOf(
        Order("ORD-1042", service = "Wash & Fold", weight = 5.2, price = 260, status = "Processing", date = "2026-07-04", paid = false),
        Order("ORD-1038", service = "Dry Clean", weight = 2.1, price = 315, status = "Ready", date = "2026-07-03", paid = false),
        Order("ORD-1035", service = "Wash & Fold", weight = 3.8, price = 190, status = "Delivered", date = "2026-07-01", paid = true),
        Order("ORD-1029", service = "Fold Only", weight = 4.0, price = 160, status = "Delivered", date = "2026-06-28", paid = true),
        Order("ORD-1021", service = "Dry Clean", weight = 1.5, price = 225, status = "Delivered", date = "2026-06-25", paid = true)
    )

    private val staffOrders = listOf(
        Order("ORD-1042", customer = "Maria Santos", service = "Wash & Fold", weight = 5.2, price = 260, status = "Processing", date = "2026-07-04", paid = false),
        Order("ORD-1041", customer = "Juan Dela Cruz", service = "Dry Clean", weight = 3.0, price = 450, status = "Pending", date = "2026-07-04", paid = false),
        Order("ORD-1040", customer = "Ana Reyes", service = "Wash & Fold", weight = 6.5, price = 325, status = "Picked Up", date = "2026-07-04", paid = false),
        Order("ORD-1038", customer = "Pedro Garcia", service = "Dry Clean", weight = 2.1, price = 315, status = "Ready", date = "2026-07-03", paid = false),
        Order("ORD-1037", customer = "Lisa Tan", service = "Fold Only", weight = 4.2, price = 168, status = "Ready", date = "2026-07-03", paid = true),
        Order("ORD-1035", customer = "Maria Santos", service = "Wash & Fold", weight = 3.8, price = 190, status = "Delivered", date = "2026-07-01", paid = true)
    )

    private val notifications = listOf(
        NotificationItem(1, "Your order ORD-1042 is now being processed.", "2 hours ago", false),
        NotificationItem(2, "Order ORD-1038 is ready for pickup!", "5 hours ago", false),
        NotificationItem(3, "Your order ORD-1035 has been delivered.", "2 days ago", true),
        NotificationItem(4, "Payment received for ORD-1035. Thank you!", "2 days ago", true)
    )

    suspend fun loadOrders(isStaff: Boolean): List<Order> {
        return if (isStaff) staffOrders else customerOrders
    }

    suspend fun loadNotifications(): List<NotificationItem> {
        return notifications
    }
}
