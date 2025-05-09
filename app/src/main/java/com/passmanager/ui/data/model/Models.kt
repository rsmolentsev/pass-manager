package com.passmanager.ui.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val masterPassword: String
)

data class PasswordEntry(
    val id: Long? = null,
    val resourceName: String,
    val username: String,
    val password: String,
    val notes: String,
    val masterPassword: String
)

data class PasswordEntryUpdate(
    val resourceName: String,
    val username: String,
    val password: String,
    val notes: String,
    val masterPassword: String
)

data class DecryptPasswordRequest(
    val masterPassword: String
)

data class DecryptPasswordResponse(
    val password: String
)

data class UserSettings(
    val autoLogoutMinutes: Int
)

data class ChangeMasterPasswordRequest(
    val oldMasterPassword: String,
    val newMasterPassword: String
)

data class AuthResponse(
    val token: String
) 