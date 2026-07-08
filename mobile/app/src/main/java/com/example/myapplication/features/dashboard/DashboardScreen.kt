package com.example.myapplication.features.dashboard

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.api.NetworkClient
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

val NOTIFICATIONS = listOf(
    DashboardNotification(1, "Your order ORD-1042 is now being processed.", "2 hours ago", false),
    DashboardNotification(2, "Order ORD-1038 is ready for pickup!", "5 hours ago", false),
    DashboardNotification(3, "Your order ORD-1035 has been delivered.", "2 days ago", true),
    DashboardNotification(4, "Payment received for ORD-1035. Thank you!", "2 days ago", true)
)

val CUSTOMER_ORDERS = listOf(
    DashboardOrder("ORD-1042", "", "Wash & Fold", 5.2, 260.0, "Processing", "2026-07-04", false),
    DashboardOrder("ORD-1038", "", "Dry Clean", 2.1, 315.0, "Ready", "2026-07-03", false),
    DashboardOrder("ORD-1035", "", "Wash & Fold", 3.8, 190.0, "Delivered", "2026-07-01", true),
    DashboardOrder("ORD-1029", "", "Fold Only", 4.0, 160.0, "Delivered", "2026-06-28", true),
    DashboardOrder("ORD-1021", "", "Dry Clean", 1.5, 225.0, "Delivered", "2026-06-25", true)
)

val STAFF_ORDERS_INITIAL = listOf(
    DashboardOrder("ORD-1042", "Maria Santos", "Wash & Fold", 5.2, 260.0, "Processing", "2026-07-04", false),
    DashboardOrder("ORD-1041", "Juan Dela Cruz", "Dry Clean", 3.0, 450.0, "Pending", "2026-07-04", false),
    DashboardOrder("ORD-1040", "Ana Reyes", "Wash & Fold", 6.5, 325.0, "Picked Up", "2026-07-04", false),
    DashboardOrder("ORD-1038", "Pedro Garcia", "Dry Clean", 2.1, 315.0, "Ready", "2026-07-03", false),
    DashboardOrder("ORD-1037", "Lisa Tan", "Fold Only", 4.2, 168.0, "Ready", "2026-07-03", true),
    DashboardOrder("ORD-1035", "Maria Santos", "Wash & Fold", 3.8, 190.0, "Delivered", "2026-07-01", true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            DashboardRepository(NetworkClient.apiService),
            sessionManager
        )
    )
) {
    val orders by viewModel.orders.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showNotifications by remember { mutableStateOf(false) }
    var staffOrders by remember { mutableStateOf(STAFF_ORDERS_INITIAL) }

    val fullName = sessionManager.getFullName() ?: "User"
    val role = sessionManager.getRole() ?: "CUSTOMER"
    val isStaff = role == "STAFF"
    val unreadCount = NOTIFICATIONS.count { !it.read }

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    LaunchedEffect(orders) {
        if (orders.isNotEmpty()) {
            staffOrders = orders
        }
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentPrimary)
        }
        return
    }

    if (error.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = error, color = ErrorRed, fontSize = 16.sp)
        }
        return
    }

    val customerOrders = if (orders.isNotEmpty()) orders else CUSTOMER_ORDERS

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
                } else {
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
                        label = { Text("Payments", fontSize = 11.sp) },
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
                    selected = false,
                    onClick = { },
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
                    fullName = fullName,
                    orders = staffOrders,
                    onStatusUpdate = { orderId ->
                        staffOrders = staffOrders.map { order ->
                            if (order.id == orderId) {
                                val next = getNextStatus(order.status)
                                if (next != null) order.copy(status = next) else order
                            } else order
                        }
                    },
                    onRecordPayment = { orderId ->
                        staffOrders = staffOrders.map { order ->
                            if (order.id == orderId) order.copy(paid = true) else order
                        }
                    }
                )
            } else {
                CustomerDashboardContent(fullName = fullName, orders = customerOrders)
            }

            if (showNotifications) {
                NotificationPanel(
                    notifications = NOTIFICATIONS,
                    onDismiss = { showNotifications = false }
                )
            }
        }
    }
}

@Composable
fun CustomerDashboardContent(fullName: String, orders: List<DashboardOrder>) {
    val activeOrders = orders.filter { it.status != "Delivered" }
    val latestOrder = activeOrders.firstOrNull()
    val totalOrders = orders.size
    val deliveredCount = orders.count { it.status == "Delivered" }
    val totalSpent = orders.filter { it.paid }.sumOf { it.price }

    fun formatCurrency(amount: Double): String {
        return "₱${"%.0f".format(amount)}"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WelcomeBanner(
                greeting = "Welcome back, ${fullName.split(" ").firstOrNull() ?: "there"}! 👋",
                subtitle = if (activeOrders.isNotEmpty())
                    "You have ${activeOrders.size} active order${if (activeOrders.size > 1) "s" else ""} in progress."
                else
                    "You have no active orders. Ready to schedule a pickup?"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Inventory2,
                    iconBg = AccentSubtle,
                    iconColor = AccentPrimary,
                    value = totalOrders.toString(),
                    label = "Total Orders"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Sync,
                    iconBg = StatusProcessingBg,
                    iconColor = StatusProcessing,
                    value = activeOrders.size.toString(),
                    label = "Active"
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.CheckCircle,
                    iconBg = StatusReadyBg,
                    iconColor = StatusReady,
                    value = deliveredCount.toString(),
                    label = "Completed"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.AccountBalanceWallet,
                    iconBg = StatusPendingBg,
                    iconColor = StatusPending,
                    value = formatCurrency(totalSpent),
                    label = "Total Spent"
                )
            }
        }

        if (latestOrder != null) {
            item {
                SectionCard(title = "📍  Active Order Tracker") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = latestOrder.id,
                                color = AccentPrimaryHover,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            StatusBadge(status = latestOrder.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${latestOrder.service} · ${latestOrder.weight}kg",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OrderStepper(currentStatus = latestOrder.status)
                    }
                }
            }
        }

        item {
            SectionCard(title = "⚡  Quick Actions") {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.CalendarMonth,
                            iconBg = AccentSubtle,
                            iconColor = AccentPrimary,
                            label = "Schedule Pickup",
                            desc = "Book a date"
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.AddCircle,
                            iconBg = StatusReadyBg,
                            iconColor = StatusReady,
                            label = "New Order",
                            desc = "Create order"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.History,
                            iconBg = StatusPendingBg,
                            iconColor = StatusPending,
                            label = "Order History",
                            desc = "View past orders"
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Person,
                            iconBg = StatusProcessingBg,
                            iconColor = StatusProcessing,
                            label = "My Profile",
                            desc = "Update info"
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "📋  Recent Orders") {
                Column {
                    orders.forEachIndexed { index, order ->
                        OrderListItem(order = order)
                        if (index < orders.size - 1) {
                            HorizontalDivider(
                                color = BorderSubtle,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun StaffDashboardContent(
    fullName: String,
    orders: List<DashboardOrder>,
    onStatusUpdate: (String) -> Unit,
    onRecordPayment: (String) -> Unit
) {
    val pendingCount = orders.count { it.status == "Pending" }
    val processingCount = orders.count { it.status in listOf("Picked Up", "Processing") }
    val readyCount = orders.count { it.status == "Ready" }
    val unpaidCount = orders.count { !it.paid && it.status != "Pending" }
    val totalRevenue = orders.filter { it.paid }.sumOf { it.price }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WelcomeBanner(
                greeting = "Good ${getTimeGreeting()}, ${fullName.split(" ").firstOrNull() ?: "Staff"}! 💼",
                subtitle = "You have $pendingCount pending and $unpaidCount unpaid orders awaiting action."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.HourglassTop,
                    iconBg = StatusPendingBg,
                    iconColor = StatusPending,
                    value = pendingCount.toString(),
                    label = "Pending"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Sync,
                    iconBg = StatusProcessingBg,
                    iconColor = StatusProcessing,
                    value = processingCount.toString(),
                    label = "In Progress"
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.CheckCircle,
                    iconBg = StatusReadyBg,
                    iconColor = StatusReady,
                    value = readyCount.toString(),
                    label = "Ready"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Payments,
                    iconBg = AccentSubtle,
                    iconColor = AccentPrimary,
                    value = "₱$totalRevenue",
                    label = "Revenue"
                )
            }
        }

        item {
            SectionCard(title = "📦  Order Queue") {
                Column {
                    val activeOrders = orders.filter { it.status != "Delivered" }
                    activeOrders.forEachIndexed { index, order ->
                        StaffOrderQueueItem(
                            order = order,
                            onStatusUpdate = { onStatusUpdate(order.id) }
                        )
                        if (index < activeOrders.size - 1) {
                            HorizontalDivider(
                                color = BorderSubtle,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "💳  Payment Status") {
                Column {
                    val payableOrders = orders.filter { it.status != "Pending" }
                    payableOrders.forEachIndexed { index, order ->
                        StaffPaymentItem(
                            order = order,
                            onRecordPayment = { onRecordPayment(order.id) }
                        )
                        if (index < payableOrders.size - 1) {
                            HorizontalDivider(
                                color = BorderSubtle,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun NotificationPanel(
    notifications: List<DashboardNotification>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(top = 80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgGlass)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                notifications.forEach { notification ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (notification.read) StatusReady else AccentPrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = notification.text, color = TextPrimary, fontSize = 14.sp)
                            Text(text = notification.time, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                    Divider(color = BorderSubtle)
                }
            }
        }
    }
}

@Composable
fun WelcomeBanner(greeting: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(AccentGradientStart, AccentGradientEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(text = greeting, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), lineHeight = 20.sp)
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgGlass),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, letterSpacing = (-0.5).sp)
            Text(text = label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgGlass),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle))
        )
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Divider(color = BorderSubtle, thickness = 1.dp)
            content()
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = STATUS_COLORS[status] ?: TextMuted
    val bgColor = STATUS_BG_COLORS[status] ?: BgElevated

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Text(text = status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PaymentBadge(paid: Boolean) {
    val color = if (paid) StatusPaid else StatusUnpaid
    val bgColor = if (paid) StatusReadyBg else ErrorRedBg

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = if (paid) "✓ Paid" else "Unpaid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun OrderStepper(currentStatus: String) {
    val currentIdx = STATUS_STEPS.indexOf(currentStatus)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        STATUS_STEPS.forEachIndexed { index, step ->
            val isCompleted = index < currentIdx
            val isActive = index == currentIdx

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> AccentPrimary
                                isActive -> Color.Transparent
                                else -> BgElevated
                            }
                        )
                        .then(if (isActive) Modifier.border(2.5.dp, AccentPrimary, CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCompleted) "✓" else "${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCompleted -> Color.White
                            isActive -> AccentPrimary
                            else -> TextMuted
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted || isActive) TextPrimary else TextMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    label: String,
    desc: String
) {
    Card(
        modifier = modifier.clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = iconColor)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = desc, fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun OrderListItem(order: DashboardOrder) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = order.id, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = order.service, color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${order.date} · ${order.weight}kg", color = TextSecondary, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "₱${order.price}", fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = order.status)
        }
    }
}

@Composable
fun StaffOrderQueueItem(order: DashboardOrder, onStatusUpdate: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = order.customer, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = order.service, color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "₱${order.price}", fontWeight = FontWeight.Bold, color = TextPrimary)
                StatusBadge(status = order.status)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onStatusUpdate,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Advance Status", color = Color.White)
        }
    }
}

@Composable
fun StaffPaymentItem(order: DashboardOrder, onRecordPayment: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = order.customer, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "${order.service} • ${order.date}", color = TextSecondary, fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentBadge(paid = order.paid)
            if (!order.paid) {
                Button(onClick = onRecordPayment, shape = RoundedCornerShape(10.dp)) {
                    Text(text = "Mark Paid")
                }
            }
        }
    }
}

fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
}

fun getTimeGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "morning"
        hour < 17 -> "afternoon"
        else -> "evening"
    }
}

fun getNextStatus(current: String): String? {
    val idx = STATUS_STEPS.indexOf(current)
    return if (idx in 0 until STATUS_STEPS.size - 1) STATUS_STEPS[idx + 1] else null
}

data class DashboardOrder(
    val id: String,
    val customer: String,
    val service: String,
    val weight: Double,
    val price: Int,
    val status: String,
    val date: String,
    val paid: Boolean
)

data class DashboardNotification(
    val id: Int,
    val text: String,
    val time: String,
    val read: Boolean
)
