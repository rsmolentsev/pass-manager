package com.passmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passmanager.ui.data.api.ApiService
import com.passmanager.ui.data.model.AuthResponse
import com.passmanager.ui.data.model.LoginRequest
import com.passmanager.ui.data.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                Log.d("AuthViewModel", "Attempting to login user: $username")
                val response = apiService.login(LoginRequest(username, password))
                Log.d("AuthViewModel", "Login response code: ${response.code()}")
                Log.d("AuthViewModel", "Login response message: ${response.message()}")
                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "Login successful")
                    _authState.value = AuthState.Success(response.body()!!)
                } else {
                    val errorMessage = "Login failed: ${response.message()}"
                    Log.e("AuthViewModel", errorMessage)
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Login failed: ${e.message}"
                Log.e("AuthViewModel", errorMessage, e)
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                Log.d("AuthViewModel", "Attempting to register user: $username")
                val response = apiService.register(RegisterRequest(username, password))
                Log.d("AuthViewModel", "Register response code: ${response.code()}")
                Log.d("AuthViewModel", "Register response message: ${response.message()}")
                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "Registration successful")
                    _authState.value = AuthState.Success(response.body()!!)
                } else {
                    val errorMessage = "Registration failed: ${response.message()}"
                    Log.e("AuthViewModel", errorMessage)
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Registration failed: ${e.message}"
                Log.e("AuthViewModel", errorMessage, e)
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Initial
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val authResponse: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
} 