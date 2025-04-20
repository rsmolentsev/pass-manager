package com.passmanager.ui.data.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    private var token: String = ""

    fun setToken(token: String) {
        this.token = token
    }

    fun getToken(): String = token

    fun clearToken() {
        token = ""
    }
} 