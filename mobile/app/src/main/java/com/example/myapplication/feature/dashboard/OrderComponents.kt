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

// ─── Order Stepper ──────────────────────────────────────────
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(64.dp)
            ) {
                // Dot
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
                        .then(
                            if (isActive) Modifier.border(2.5.dp, AccentPrimary, CircleShape)
                            else Modifier
                        ),
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

// ─── Quick Action Button ────────────────────────────────────
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
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Order List Item (Customer) ─────────────────────────────
@Composable
fun OrderListItem(order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = order.id,
                    color = AccentPrimaryHover,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${SERVICE_ICONS[order.service] ?: "🧺"} ${order.service} · ${order.weight}kg",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₱${order.price}",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            PaymentBadge(paid = order.paid)
        }
    }
}

// ─── All Orders List Item (staff-facing, includes customer + date) ──
@Composable
fun AllOrdersListItem(order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = order.id,
                    color = AccentPrimaryHover,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.customer,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "📍 ${order.address}",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${SERVICE_ICONS[order.service] ?: "🧺"} ${order.service} · ${order.weight}kg · ${order.date}",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₱${order.price}",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            PaymentBadge(paid = order.paid)
        }
    }
}

// ─── Staff Order Card (status advancement + payment recording, combined) ──
@Composable
fun StaffOrderCard(order: Order, onStatusUpdate: () -> Unit, onRecordPayment: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = order.id,
                        color = AccentPrimaryHover,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = order.customer,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "📍 ${order.address}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${SERVICE_ICONS[order.service] ?: "🧺"} ${order.service} · ${order.weight}kg · ₱${order.price}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = order.status)
                Spacer(modifier = Modifier.height(6.dp))
                PaymentBadge(paid = order.paid)
            }
        }

        // Status advance action — available from Pending through Ready (BR-003 sequence)
        val nextStatus = getNextStatus(order.status)
        // Payment action — available from Picked Up through Delivered, matching web (status != Pending)
        val canRecordPayment = order.status != "Pending" && !order.paid

        if (nextStatus != null || canRecordPayment) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (nextStatus != null) {
                    Button(
                        onClick = onStatusUpdate,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = "→ Move to $nextStatus",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (canRecordPayment) {
                    Button(
                        onClick = onRecordPayment,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusReady,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Record Payment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

