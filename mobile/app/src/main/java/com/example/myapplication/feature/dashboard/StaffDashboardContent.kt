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
// ─── Staff Dashboard Content ────────────────────────────────
// ═══════════════════════════════════════════════════════════════
// activeTab controls which bottom-nav view is shown:
//   "dashboard" -> stats + a single combined Order Queue where each order shows
//                  both the status-advance action and the payment-recording action
//                  together (matches the web app's side-by-side layout, adapted for
//                  a single-column phone screen)
//   "orders"    -> the full, read-only All Orders list (matches the web app's
//                  comprehensive order history table)
@Composable
fun StaffDashboardContent(
    fullName: String,
    orders: List<Order>,
    activeTab: String,
    onStatusUpdate: (Long) -> Unit,
    onRecordPayment: (Long) -> Unit,
    errorMessage: String? = null
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
        if (activeTab == "orders") {
            // ── Orders Tab: full read-only history, matches the web All Orders table ──
            item {
                SectionCard(title = "📋  All Orders") {
                    if (orders.isEmpty()) {
                        Text(
                            text = "No orders yet.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column {
                            orders.forEachIndexed { index, order ->
                                AllOrdersListItem(order = order)
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
            }
        } else {
            // ── Dashboard Tab: stats + combined actionable Order Queue ──
            item {
                WelcomeBanner(
                    greeting = "Good ${getTimeGreeting()}, ${fullName.split(" ").firstOrNull() ?: "Staff"}! 💼",
                    subtitle = "You have $pendingCount pending and $unpaidCount unpaid orders awaiting action."
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorRedBg, RoundedCornerShape(10.dp))
                            .padding(12.dp)
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

            // Combined Order Queue: status advancement + payment recording together,
            // for every order still needing attention (not yet Delivered AND Paid).
            item {
                SectionCard(title = "📦  Order Queue") {
                    Column {
                        val actionableOrders = orders.filter { it.status != "Delivered" || !it.paid }
                        if (actionableOrders.isEmpty()) {
                            Text(
                                text = "No orders need attention right now.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            actionableOrders.forEachIndexed { index, order ->
                                StaffOrderCard(
                                    order = order,
                                    onStatusUpdate = { onStatusUpdate(order.orderId) },
                                    onRecordPayment = { onRecordPayment(order.orderId) }
                                )
                                if (index < actionableOrders.size - 1) {
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
        }

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
