package com.passmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passmanager.ui.data.api.ApiService
import com.passmanager.ui.data.model.PasswordEntry
import com.passmanager.ui.data.model.PasswordEntryUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _passwords = MutableStateFlow<List<PasswordEntry>>(emptyList())
    val passwords: StateFlow<List<PasswordEntry>> = _passwords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPasswords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPasswords()
                if (response.isSuccessful) {
                    _passwords.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load passwords: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load passwords: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPassword(password: PasswordEntry) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.addPassword(password)
                if (response.isSuccessful) {
                    loadPasswords()
                    _error.value = null
                } else {
                    _error.value = "Failed to add password: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to add password: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(password: PasswordEntry) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updateRequest = PasswordEntryUpdate(
                    resourceName = password.resourceName,
                    username = password.username,
                    password = password.password,
                    notes = password.notes
                )
                val response = apiService.updatePassword(password.id!!, updateRequest)
                if (response.isSuccessful) {
                    loadPasswords()
                    _error.value = null
                } else {
                    _error.value = "Failed to update password: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update password: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePassword(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deletePassword(id)
                if (response.isSuccessful) {
                    loadPasswords()
                    _error.value = null
                } else {
                    _error.value = "Failed to delete password: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete password: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 