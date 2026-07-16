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
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.SessionManager
import com.example.myapplication.feature.profile.ProfileSheet
import kotlinx.coroutines.launch
import java.util.Calendar

// ═══════════════════════════════════════════════════════════════
// ─── Main Dashboard Screen ──────────────────────────────────
// ═══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    var profileFullName by remember { mutableStateOf(sessionManager.getFullName() ?: "User") }
    val role = sessionManager.getRole() ?: "CUSTOMER"
    val isStaff = role == "STAFF"
    val userId = sessionManager.getUserId()

    var showProfileSheet by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showNewOrderSheet by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("dashboard") } // "dashboard" | "orders" (both roles)
    var staffActionError by remember { mutableStateOf<String?>(null) }
    var staffOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var customerOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun refreshOrders() {
        if (isStaff) {
            staffOrders = DashboardRepository.loadOrders(isStaff = true)
        } else {
            customerOrders = DashboardRepository.loadOrders(isStaff = false, userId = userId)
        }
    }

    suspend fun refreshNotifications() {
        notifications = DashboardRepository.loadNotifications(userId)
    }

    LaunchedEffect(isStaff) {
        refreshOrders()
        refreshNotifications()
    }

    val unreadCount = notifications.count { !it.read }

    fun toggleNotifications() {
        val opening = !showNotifications
        showNotifications = opening
        if (opening && unreadCount > 0) {
            coroutineScope.launch {
                DashboardRepository.markNotificationsRead(userId)
                refreshNotifications()
            }
        }
    }

    Scaffold(
        topBar = {
            // Top App Bar
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
                    // Notification bell
                    Box {
                        IconButton(onClick = { toggleNotifications() }) {
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
                    // Logout
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
            // Bottom Navigation
            NavigationBar(
                containerColor = BgSecondary,
                contentColor = TextSecondary,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "dashboard",
                    onClick = { activeTab = "dashboard" },
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
                    selected = activeTab == "orders",
                    onClick = { activeTab = "orders" },
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
                        onClick = { showNewOrderSheet = true },
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
                NavigationBarItem(
                    selected = showProfileSheet,
                    onClick = { showProfileSheet = true },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = { Text("Profile", fontSize = 11.sp) },
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
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isStaff) {
                StaffDashboardContent(
                    fullName = profileFullName,
                    orders = staffOrders,
                    activeTab = activeTab,
                    onStatusUpdate = { orderId ->
                        coroutineScope.launch {
                            val result = DashboardRepository.advanceOrderStatus(orderId)
                            result.onSuccess {
                                staffActionError = null
                                refreshOrders()
                                refreshNotifications()
                            }
                            result.onFailure { staffActionError = it.message }
                        }
                    },
                    onRecordPayment = { orderId ->
                        coroutineScope.launch {
                            val result = DashboardRepository.markOrderAsPaid(orderId)
                            result.onSuccess {
                                staffActionError = null
                                refreshOrders()
                            }
                            result.onFailure { staffActionError = it.message }
                        }
                    },
                    errorMessage = staffActionError
                )
            } else {
                CustomerDashboardContent(
                    fullName = profileFullName,
                    customerOrders = customerOrders,
                    activeTab = activeTab,
                    onNavigateToOrders = { activeTab = "orders" },
                    onOpenProfile = { showProfileSheet = true }
                )
            }

            // Notification overlay
            if (showNotifications) {
                NotificationPanel(
                    notifications = notifications,
                    onDismiss = { showNotifications = false }
                )
            }

            // New Order sheet (FR-004 + FR-005)
            if (showNewOrderSheet) {
                NewOrderSheet(
                    userId = userId,
                    onDismiss = { showNewOrderSheet = false },
                    onOrderCreated = {
                        showNewOrderSheet = false
                        coroutineScope.launch {
                            refreshOrders()
                            refreshNotifications()
                        }
                    }
                )
            }

            // Profile Sheet (FR-003)
            if (showProfileSheet) {
                ProfileSheet(
                    sessionManager = sessionManager,
                    onDismiss = { showProfileSheet = false },
                    onProfileUpdated = { newName ->
                        profileFullName = newName
                    }
                )
            }
        }
    }
}

