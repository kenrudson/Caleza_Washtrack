package com.example.myapplication.features.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.ErrorResponse
import com.example.myapplication.utils.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel(
    private val repository: RegisterRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun register(request: com.example.myapplication.api.RegisterRequest, onSuccess: () -> Unit) {
        _error.value = ""
        _loading.value = true

        viewModelScope.launch {
            try {
                val response = repository.register(request)
                handleResponse(response, onSuccess)
            } catch (e: Exception) {
                _error.value = "Connection error. Please try again."
            } finally {
                _loading.value = false
            }
        }
    }

    private fun handleResponse(response: Response<*>, onSuccess: () -> Unit) {
        if (response.isSuccessful && response.body() != null) {
            val authRes = response.body() as com.example.myapplication.api.AuthResponse
            sessionManager.saveSession(
                token = authRes.token,
                userId = authRes.userId,
                fullName = authRes.fullName,
                email = authRes.email,
                role = authRes.role
            )
            onSuccess()
        } else {
            val errorBody = response.errorBody()?.string()
            _error.value = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
            } catch (e: Exception) {
                "Registration failed. Please check inputs."
            }
        }
    }
}
