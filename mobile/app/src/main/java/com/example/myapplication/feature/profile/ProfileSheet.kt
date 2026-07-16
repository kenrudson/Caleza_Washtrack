package com.example.myapplication.feature.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.SessionManager
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
    sessionManager: SessionManager,
    onDismiss: () -> Unit,
    onProfileUpdated: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userId = sessionManager.getUserId()
    var activeTab by remember { mutableStateOf("info") } // "info" | "password"
    // Personal Info States
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var infoLoading by remember { mutableStateOf(true) }
    var infoSaving by remember { mutableStateOf(false) }
    var infoError by remember { mutableStateOf<String?>(null) }
    var infoSuccess by remember { mutableStateOf<String?>(null) }
    // Password States
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var pwSaving by remember { mutableStateOf(false) }
    var pwError by remember { mutableStateOf<String?>(null) }
    var pwSuccess by remember { mutableStateOf<String?>(null) }
    // Fetch profile data on launch
    LaunchedEffect(userId) {
        ProfileRepository.getProfile(userId).onSuccess { profile ->
            email = profile.email
            fullName = profile.fullName
            phone = profile.phone
            address = profile.address
            role = profile.role
            infoLoading = false
        }.onFailure {
            infoError = it.message ?: "Failed to load profile"
            infoLoading = false
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BgSecondary) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Profile", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Profile Hero Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(AccentSubtle, Color.Transparent)))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar Large
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF1E40AF), Color(0xFF3B82F6)))),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = if (fullName.isNotEmpty()) {
                        fullName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
                    } else "U"
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column {
                    Text(if (fullName.isNotEmpty()) fullName else "Loading...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(if (email.isNotEmpty()) email else "—", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AccentSubtle)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(if (role.isNotEmpty()) role else "CUSTOMER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Tabs Switcher
            TabRow(
                selectedTabIndex = if (activeTab == "info") 0 else 1,
                containerColor = Color.Transparent,
                contentColor = AccentPrimary
            ) {
                Tab(
                    selected = activeTab == "info",
                    onClick = { activeTab = "info"; infoError = null; infoSuccess = null },
                    text = { Text("👤 Info", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                )
                Tab(
                    selected = activeTab == "password",
                    onClick = { activeTab = "password"; pwError = null; pwSuccess = null },
                    text = { Text("🔒 Password", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (infoLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            } else if (activeTab == "info") {
                // Personal Info Form
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Email (read-only)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BorderSubtle,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextMuted,
                        unfocusedTextColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (infoError != null) {
                    Text(
                        infoError!!,
                        color = StatusUnpaid,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorRedBg)
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                if (infoSuccess != null) {
                    Text(
                        infoSuccess!!,
                        color = StatusReady,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusReadyBg)
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            if (fullName.isBlank() || phone.isBlank() || address.isBlank()) {
                                infoError = "Please fill in all required fields."
                                return@Button
                            }
                            infoSaving = true
                            infoError = null
                            infoSuccess = null
                            coroutineScope.launch {
                                ProfileRepository.updateProfile(userId, fullName, phone, address)
                                    .onSuccess { updated ->
                                        sessionManager.saveSession(
                                            token = sessionManager.getToken() ?: "",
                                            userId = userId,
                                            fullName = updated.fullName,
                                            email = updated.email,
                                            role = updated.role
                                        )
                                        infoSuccess = "Profile updated successfully!"
                                        onProfileUpdated(updated.fullName)
                                    }.onFailure {
                                        infoError = it.message ?: "Failed to update profile"
                                    }
                                infoSaving = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                        modifier = Modifier.weight(1f),
                        enabled = !infoSaving
                    ) {
                        if (infoSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Save Changes", color = Color.White)
                        }
                    }
                }
            } else {
                // Change Password Form
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (pwError != null) {
                    Text(
                        pwError!!,
                        color = StatusUnpaid,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorRedBg)
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                if (pwSuccess != null) {
                    Text(
                        pwSuccess!!,
                        color = StatusReady,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusReadyBg)
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                                pwError = "Please fill in all required fields."
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                pwError = "New passwords do not match."
                                return@Button
                            }
                            if (newPassword.length < 8) {
                                pwError = "New password must be at least 8 characters."
                                return@Button
                            }
                            pwSaving = true
                            pwError = null
                            pwSuccess = null
                            coroutineScope.launch {
                                ProfileRepository.changePassword(userId, currentPassword, newPassword)
                                    .onSuccess {
                                        pwSuccess = "Password updated successfully!"
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    }.onFailure {
                                        pwError = it.message ?: "Failed to change password"
                                    }
                                pwSaving = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                        modifier = Modifier.weight(1f),
                        enabled = !pwSaving
                    ) {
                        if (pwSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Change Password", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
