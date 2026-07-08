package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myapplication.features.dashboard.DashboardScreen
import com.example.myapplication.features.login.LoginScreen
import com.example.myapplication.features.registration.RegisterScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = SessionManager(this)

        setContent {
            MyApplicationTheme {
                var currentScreen by remember {
                    mutableStateOf(
                        if (sessionManager.isLoggedIn()) Screen.Dashboard else Screen.Login
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.Login -> {
                                LoginScreen(
                                    sessionManager = sessionManager,
                                    onLoginSuccess = { currentScreen = Screen.Dashboard },
                                    onNavigateToRegister = { currentScreen = Screen.Register }
                                )
                            }
                            Screen.Register -> {
                                RegisterScreen(
                                    sessionManager = sessionManager,
                                    onRegisterSuccess = { currentScreen = Screen.Dashboard },
                                    onNavigateToLogin = { currentScreen = Screen.Login }
                                )
                            }
                            Screen.Dashboard -> {
                                DashboardScreen(
                                    sessionManager = sessionManager,
                                    onLogout = { currentScreen = Screen.Login }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Login,
    Register,
    Dashboard
}