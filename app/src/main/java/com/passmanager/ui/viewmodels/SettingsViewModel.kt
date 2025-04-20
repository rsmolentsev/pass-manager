package com.passmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passmanager.ui.data.api.ApiService
import com.passmanager.ui.data.model.ChangeMasterPasswordRequest
import com.passmanager.ui.data.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _settings = MutableStateFlow<UserSettings?>(null)
    val settings: StateFlow<UserSettings?> = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getSettings()
                if (response.isSuccessful) {
                    _settings.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Failed to load settings: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updateSettings(settings)
                if (response.isSuccessful) {
                    _settings.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Failed to update settings: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeMasterPassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.changeMasterPassword(
                    ChangeMasterPasswordRequest(oldPassword, newPassword)
                )
                if (response.isSuccessful) {
                    _error.value = null
                } else {
                    _error.value = "Failed to change master password: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to change master password: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 