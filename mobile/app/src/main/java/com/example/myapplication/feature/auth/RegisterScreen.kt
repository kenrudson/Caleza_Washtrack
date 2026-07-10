package com.example.myapplication.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.feature.auth.AuthRepository
import com.example.myapplication.feature.auth.ErrorResponse
import com.example.myapplication.feature.auth.RegisterRequest
import com.example.myapplication.utils.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    sessionManager: SessionManager,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val darkBackground = Color(0xFF242424)
    val cardBackground = Color(0xFF1A1A1A)
    val accentColor = Color(0xFF646CFF)
    val textColor = Color(0xFFEDEDED)
    val placeholderColor = Color(0xFF888888)
    val errorColor = Color(0xFFE57373)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp, horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Account",
                    color = textColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; error = "" },
                    label = { Text("Full Name", color = placeholderColor) },
                    placeholder = { Text("Enter your full name", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name", tint = placeholderColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; error = "" },
                    label = { Text("Email", color = placeholderColor) },
                    placeholder = { Text("Enter your email", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = placeholderColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; error = "" },
                    label = { Text("Phone Number", color = placeholderColor) },
                    placeholder = { Text("Enter your phone number", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Number", tint = placeholderColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; error = "" },
                    label = { Text("Address", color = placeholderColor) },
                    placeholder = { Text("Enter your address", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Address", tint = placeholderColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = "" },
                    label = { Text("Password", color = placeholderColor) },
                    placeholder = { Text("Min. 8 characters", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = placeholderColor) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                tint = placeholderColor
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; error = "" },
                    label = { Text("Confirm Password", color = placeholderColor) },
                    placeholder = { Text("Confirm your password", color = placeholderColor) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password", tint = placeholderColor) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password",
                                tint = placeholderColor
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF3A3A3A),
                        cursorColor = accentColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = errorColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank() || password.isBlank()) {
                            error = "All fields are required"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            error = "Passwords do not match"
                            return@Button
                        }
                        if (password.length < 8) {
                            error = "Password must be at least 8 characters"
                            return@Button
                        }

                        loading = true
                        error = ""
                        focusManager.clearFocus()

                        coroutineScope.launch {
                            try {
                                val request = RegisterRequest(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    address = address.trim(),
                                    password = password
                                )
                                val response = withContext(Dispatchers.IO) {
                                    AuthRepository.register(request)
                                }
                                if (response.isSuccessful && response.body() != null) {
                                    val authRes = response.body()!!
                                    sessionManager.saveSession(
                                        token = authRes.token,
                                        userId = authRes.userId,
                                        fullName = authRes.fullName,
                                        email = authRes.email,
                                        role = authRes.role
                                    )
                                    onRegisterSuccess()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    error = try {
                                        Gson().fromJson(errorBody, ErrorResponse::class.java).message
                                    } catch (e: Exception) {
                                        "Registration failed. Please check inputs."
                                    }
                                }
                            } catch (e: Exception) {
                                error = "Connection error. Please try again."
                            } finally {
                                loading = false
                            }
                        }
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        disabledContainerColor = accentColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = textColor,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Creating account...", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text(text = "Register", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Already have an account? Log in",
                        color = accentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
