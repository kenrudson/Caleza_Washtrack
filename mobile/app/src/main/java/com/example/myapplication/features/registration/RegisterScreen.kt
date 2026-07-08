package com.example.myapplication.features.registration

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.api.RegisterRequest
import com.example.myapplication.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    sessionManager: SessionManager,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(RegisterRepository(com.example.myapplication.api.NetworkClient.apiService), sessionManager)
    )
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
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

                RegistrationField(value = fullName, onValueChange = { fullName = it }, label = "Full Name", icon = Icons.Default.Person, placeholder = "Enter your full name", focusManager = focusManager)
                RegistrationField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email, placeholder = "Enter your email", keyboardType = KeyboardType.Email, focusManager = focusManager)
                RegistrationField(value = phone, onValueChange = { phone = it }, label = "Phone Number", icon = Icons.Default.Phone, placeholder = "Enter your phone number", focusManager = focusManager)
                RegistrationField(value = address, onValueChange = { address = it }, label = "Address", icon = Icons.Default.Home, placeholder = "Enter your address", focusManager = focusManager)
                PasswordField(value = password, onValueChange = { password = it }, label = "Password", visible = passwordVisible, onToggleVisible = { passwordVisible = !passwordVisible }, focusManager = focusManager)
                PasswordField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", visible = confirmPasswordVisible, onToggleVisible = { confirmPasswordVisible = !confirmPasswordVisible }, focusManager = focusManager)

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
                            return@Button
                        }
                        if (password != confirmPassword) {
                            return@Button
                        }
                        viewModel.register(RegisterRequest(fullName, email, phone, address, password), onRegisterSuccess)
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

@Composable
private fun RegistrationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF888888)) },
        placeholder = { Text(placeholder, color = Color(0xFF888888)) },
        leadingIcon = { Icon(icon, contentDescription = label, tint = Color(0xFF888888)) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFEDEDED),
            unfocusedTextColor = Color(0xFFEDEDED),
            focusedBorderColor = Color(0xFF646CFF),
            unfocusedBorderColor = Color(0xFF3A3A3A),
            cursorColor = Color(0xFF646CFF)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF888888)) },
        placeholder = { Text(if (label.contains("Confirm")) "Confirm your password" else "Min. 8 characters", color = Color(0xFF888888)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = label, tint = Color(0xFF888888)) },
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (visible) "Hide Password" else "Show Password",
                    tint = Color(0xFF888888)
                )
            }
        },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFEDEDED),
            unfocusedTextColor = Color(0xFFEDEDED),
            focusedBorderColor = Color(0xFF646CFF),
            unfocusedBorderColor = Color(0xFF3A3A3A),
            cursorColor = Color(0xFF646CFF)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}
