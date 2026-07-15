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
import kotlinx.coroutines.launch
import java.util.Calendar

// ═══════════════════════════════════════════════════════════════
// ─── Customer Dashboard Content ─────────────────────────────
// ═══════════════════════════════════════════════════════════════
@Composable
fun CustomerDashboardContent(
    fullName: String,
    customerOrders: List<Order>,
    activeTab: String = "dashboard",
    onNavigateToOrders: () -> Unit = {}
) {
    val activeOrders = customerOrders.filter { it.status != "Delivered" }
    val latestOrder = activeOrders.firstOrNull()
    val totalOrders = customerOrders.size
    val deliveredCount = customerOrders.count { it.status == "Delivered" }
    val totalSpent = customerOrders.filter { it.paid }.sumOf { it.price }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activeTab == "orders") {
            // ── Orders Tab: dedicated, full order history (FR-011) ──
            item {
                SectionCard(title = "📋  Order History") {
                    if (customerOrders.isEmpty()) {
                        Text(
                            text = "No orders yet. Tap New Order to get started.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column {
                            customerOrders.forEachIndexed { index, order ->
                                OrderListItem(order = order)
                                if (index < customerOrders.size - 1) {
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
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        } else {
        // Welcome Banner
        item {
            WelcomeBanner(
                greeting = "Welcome back, ${fullName.split(" ").firstOrNull() ?: "there"}! 👋",
                subtitle = if (activeOrders.isNotEmpty())
                    "You have ${activeOrders.size} active order${if (activeOrders.size > 1) "s" else ""} in progress."
                else
                    "You have no active orders. Ready to schedule a pickup?"
            )
        }

        // Stats Row
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
                    value = "₱$totalSpent",
                    label = "Total Spent"
                )
            }
        }

        // Active Order Tracker
        if (latestOrder != null) {
            item {
                SectionCard(title = "📍  Active Order Tracker") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Order info row
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

        // Quick Actions
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
                            desc = "View past orders",
                            onClick = onNavigateToOrders
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

        // Recent Orders
        item {
            SectionCard(title = "📋  Recent Orders") {
                Column {
                    customerOrders.forEachIndexed { index, order ->
                        OrderListItem(order = order)
                        if (index < customerOrders.size - 1) {
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

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

