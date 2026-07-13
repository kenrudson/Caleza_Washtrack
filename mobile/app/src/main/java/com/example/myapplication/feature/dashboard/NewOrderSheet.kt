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
// ─── New Order Sheet (FR-004 + FR-005) ──────────────────────
// ═══════════════════════════════════════════════════════════════
private val TIME_SLOTS = listOf(
    "8:00 AM - 11:00 AM",
    "11:00 AM - 2:00 PM",
    "2:00 PM - 5:00 PM",
    "5:00 PM - 8:00 PM"
)

private val SERVICE_TYPE_OPTIONS = listOf(
    "WASH_FOLD" to "Wash & Fold",
    "DRY_CLEAN" to "Dry Clean",
    "FOLD_ONLY" to "Fold Only"
)

private fun tomorrowDateString(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, 1)
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(year, month, day)
}

private fun todayDateString(): String {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(year, month, day)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderSheet(
    userId: Long,
    onDismiss: () -> Unit,
    onOrderCreated: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var pickupAddress by remember { mutableStateOf("") }
    var scheduledDate by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf(TIME_SLOTS[0]) }
    var pickupNotes by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf(SERVICE_TYPE_OPTIONS[0].first) }
    var weightKg by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var serviceMenuExpanded by remember { mutableStateOf(false) }
    var timeSlotMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BgSecondary) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("New Order", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            Text("PICKUP DETAILS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Pickup Address *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = scheduledDate,
                onValueChange = { scheduledDate = it },
                label = { Text("Pickup Date * (YYYY-MM-DD)") },
                placeholder = { Text(tomorrowDateString()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(
                expanded = timeSlotMenuExpanded,
                onExpandedChange = { timeSlotMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time Slot *") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeSlotMenuExpanded) }
                )
                ExposedDropdownMenu(expanded = timeSlotMenuExpanded, onDismissRequest = { timeSlotMenuExpanded = false }) {
                    TIME_SLOTS.forEach { slot ->
                        DropdownMenuItem(text = { Text(slot) }, onClick = {
                            timeSlot = slot
                            timeSlotMenuExpanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pickupNotes,
                onValueChange = { pickupNotes = it },
                label = { Text("Pickup Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text("ORDER DETAILS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = serviceMenuExpanded,
                onExpandedChange = { serviceMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = SERVICE_TYPE_OPTIONS.first { it.first == serviceType }.second,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Service Type *") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceMenuExpanded) }
                )
                ExposedDropdownMenu(expanded = serviceMenuExpanded, onDismissRequest = { serviceMenuExpanded = false }) {
                    SERVICE_TYPE_OPTIONS.forEach { (value, label) ->
                        DropdownMenuItem(text = { Text(label) }, onClick = {
                            serviceType = value
                            serviceMenuExpanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = weightKg,
                onValueChange = { weightKg = it },
                label = { Text("Estimated Weight (kg) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = specialInstructions,
                onValueChange = { specialInstructions = it },
                label = { Text("Special Instructions (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            error?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = ErrorRed, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    error = null
                    val weight = weightKg.toDoubleOrNull()

                    when {
                        pickupAddress.isBlank() || scheduledDate.isBlank() -> {
                            error = "Please fill in all required fields."
                        }
                        weight == null || weight <= 0 || weight > 50 -> {
                            error = "Weight must be greater than 0 kg and at most 50 kg." // BR-008
                        }
                        scheduledDate <= todayDateString() -> {
                            error = "Pickup must be scheduled at least 24 hours in advance." // BR-002
                        }
                        else -> {
                            loading = true
                            coroutineScope.launch {
                                val result = DashboardRepository.submitNewOrder(
                                    NewOrderRequest(
                                        userId = userId,
                                        pickupAddress = pickupAddress,
                                        scheduledDate = scheduledDate,
                                        timeSlot = timeSlot,
                                        pickupNotes = pickupNotes.ifBlank { null },
                                        serviceType = serviceType,
                                        weightKg = weight,
                                        specialInstructions = specialInstructions.ifBlank { null }
                                    )
                                )
                                loading = false
                                result.onSuccess { onOrderCreated() }
                                result.onFailure { error = it.message ?: "Could not create order." }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
            ) {
                Text(if (loading) "Placing order..." else "Place Order", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}