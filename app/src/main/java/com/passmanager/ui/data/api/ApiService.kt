package com.passmanager.ui.data.api

import com.passmanager.ui.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("passwords")
    suspend fun getPasswords(): Response<List<PasswordEntry>>

    @GET("passwords/{id}")
    suspend fun getPassword(@Path("id") id: Long): Response<PasswordEntry>

    @POST("passwords")
    suspend fun addPassword(@Body password: PasswordEntryUpdate): Response<PasswordEntry>

    @PUT("passwords/{id}")
    suspend fun updatePassword(
        @Path("id") id: Long,
        @Body password: PasswordEntryUpdate
    ): Response<PasswordEntry>

    @DELETE("passwords/{id}")
    suspend fun deletePassword(@Path("id") id: Long): Response<Unit>

    @POST("passwords/{id}/decrypt")
    suspend fun decryptPassword(
        @Path("id") id: Long,
        @Body request: DecryptPasswordRequest
    ): Response<DecryptPasswordResponse>

    @GET("settings")
    suspend fun getSettings(): Response<UserSettings>

    @PUT("settings")
    suspend fun updateSettings(@Body settings: UserSettings): Response<UserSettings>

    @PUT("change-master-password")
    suspend fun changeMasterPassword(@Body request: ChangeMasterPasswordRequest): Response<Unit>
} 