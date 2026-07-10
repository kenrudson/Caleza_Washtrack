package com.example.myapplication.feature.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.feature.dashboard.DashboardRepository
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.SessionManager

val STATUS_STEPS = listOf("Pending", "Delivered", "Processing", "Ready", "Picked Up")

val STATUS_COLORS = mapOf(
    "Pending" to StatusPending,
    "Picked Up" to StatusPickedUp,
    "Processing" to StatusProcessing,
    "Ready" to StatusReady,
    "Delivered" to StatusDelivered
)

val STATUS_BG_COLORS = mapOf(
    "Pending" to StatusPendingBg,
    "Picked Up" to StatusPickedUpBg,
    "Processing" to StatusProcessingBg,
    "Ready" to StatusReadyBg,
    "Delivered" to StatusDeliveredBg
)

val SERVICE_ICONS = mapOf(
    "Wash & Fold" to "🧺",
    "Dry Clean" to "👔",
    "Fold Only" to "👕"
)

fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
}

fun getNextStatus(current: String): String? {
    val idx = STATUS_STEPS.indexOf(current)
    return if (idx in 0 until STATUS_STEPS.size - 1) STATUS_STEPS[idx + 1] else null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val fullName = sessionManager.getFullName() ?: "User"
    val role = sessionManager.getRole() ?: "CUSTOMER"
    val isStaff = role == "STAFF"

    var showNotifications by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf(emptyList<Order>()) }
    var notifications by remember { mutableStateOf(emptyList<NotificationItem>()) }

    val unreadCount = notifications.count { !it.read }

    LaunchedEffect(isStaff) {
        orders = DashboardRepository.loadOrders(isStaff)
        notifications = DashboardRepository.loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isStaff) "Staff Dashboard" else "My Dashboard",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "WashTrack",
                            fontSize = 11.sp,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showNotifications = !showNotifications }) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = TextSecondary
                            )
                        }
                        if (unreadCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp),
                                containerColor = ErrorRed,
                                contentColor = Color.White
                            ) {
                                Text(unreadCount.toString(), fontSize = 9.sp)
                            }
                        }
                    }
                    IconButton(onClick = {
                        sessionManager.clearSession()
                        onLogout()
                    }) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = "Logout",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgSecondary,
                    scrolledContainerColor = BgSecondary
                )
            )
        },
        containerColor = BgPrimary,
        bottomBar = {
            NavigationBar(
                containerColor = BgSecondary,
                contentColor = TextSecondary,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = AccentSubtle
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Receipt, contentDescription = null) },
                    label = { Text(if (isStaff) "Orders" else "My Orders", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPrimary,
                        selectedTextColor = AccentPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = AccentSubtle
                    )
                )
                if (!isStaff) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                        label = { Text("New Order", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPrimary,
                            selectedTextColor = AccentPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = AccentSubtle
                        )
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                text = "Welcome back, $fullName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Orders",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                itemsIndexed(orders) { _, order ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BgSecondary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(order.id, fontWeight = FontWeight.Bold, color = TextPrimary)
                            if (isStaff) {
                                Text(order.customer, color = TextSecondary)
                            }
                            Text(order.service, color = TextSecondary)
                            Text("Status: ${order.status}", color = TextPrimary)
                            Text("Price: ₱${order.price}", color = TextPrimary)
                            Text("Paid: ${if (order.paid) "Yes" else "No"}", color = TextPrimary)
                            if (isStaff && !order.paid) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Record Payment")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
