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

// ─── Constants ──────────────────────────────────────────────
val STATUS_STEPS = listOf("Pending", "Picked Up", "Processing", "Ready", "Delivered")

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

