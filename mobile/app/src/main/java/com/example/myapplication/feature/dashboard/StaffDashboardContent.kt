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
@Composable
fun StaffDashboardContent(
    fullName: String,
    orders: List<Order>,
    onStatusUpdate: (Long) -> Unit,
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
        // Welcome Banner
        item {
            WelcomeBanner(
                greeting = "Good ${getTimeGreeting()}, ${fullName.split(" ").firstOrNull() ?: "Staff"}! 💼",
                subtitle = "You have $pendingCount pending and $unpaidCount unpaid orders awaiting action."
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

        // Order Queue
        item {
            SectionCard(title = "📦  Order Queue") {
                Column {
                    val activeOrders = orders.filter { it.status != "Delivered" }
                    activeOrders.forEachIndexed { index, order ->
                        StaffOrderQueueItem(
                            order = order,
                            onStatusUpdate = { onStatusUpdate(order.orderId) }
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

        // Payment Status
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

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

