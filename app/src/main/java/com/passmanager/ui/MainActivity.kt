package com.passmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.passmanager.ui.data.model.UserSettings
import com.passmanager.ui.navigation.NavGraph
import com.passmanager.ui.screens.auth.LoginScreen
import com.passmanager.ui.screens.auth.RegisterScreen
import com.passmanager.ui.screens.passwords.PasswordFormScreen
import com.passmanager.ui.screens.passwords.PasswordListScreen
import com.passmanager.ui.screens.settings.SettingsScreen
import com.passmanager.ui.theme.PassManagerTheme
import com.passmanager.ui.viewmodels.AuthViewModel
import com.passmanager.ui.viewmodels.PasswordViewModel
import com.passmanager.ui.viewmodels.SettingsViewModel
import com.passmanager.ui.viewmodels.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val passwordViewModel: PasswordViewModel = hiltViewModel()
                    val settingsViewModel: SettingsViewModel = hiltViewModel()

                    val authState by authViewModel.authState.collectAsState()
                    val passwords by passwordViewModel.passwords.collectAsState()
                    val isLoading by passwordViewModel.isLoading.collectAsState()
                    val settings by settingsViewModel.settings.collectAsState()

                    // Handle authentication state changes
                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.Success -> {
                                val successState = authState as AuthState.Success
                                if (successState.authResponse.token.isNotBlank()) {
                                    // Login success - load passwords and settings, then navigate to main screen
                                    passwordViewModel.loadPasswords()
                                    settingsViewModel.loadSettings()
                                    navController.navigate(NavGraph.Main.passwords) {
                                        popUpTo(NavGraph.Auth.login) { inclusive = true }
                                    }
                                } else {
                                    // Registration success - navigate back to login
                                    navController.navigate(NavGraph.Auth.login)
                                }
                            }
                            else -> {}
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = NavGraph.Auth.login
                    ) {
                        // Auth Navigation
                        composable(NavGraph.Auth.login) {
                            LoginScreen(
                                navController = navController,
                                onLogin = { username, password ->
                                    authViewModel.login(username, password)
                                }
                            )
                        }
                        composable(NavGraph.Auth.register) {
                            RegisterScreen(
                                navController = navController,
                                onRegister = { username, password ->
                                    authViewModel.register(username, password)
                                }
                            )
                        }

                        // Main Navigation
                        composable(NavGraph.Main.passwords) {
                            PasswordListScreen(
                                navController = navController,
                                passwords = passwords,
                                onDeletePassword = { id ->
                                    passwordViewModel.deletePassword(id)
                                },
                                isLoading = isLoading
                            )
                        }
                        composable(NavGraph.Main.addPassword) {
                            PasswordFormScreen(
                                navController = navController,
                                password = null,
                                onSave = { password ->
                                    passwordViewModel.addPassword(password)
                                    navController.navigateUp()
                                },
                                isLoading = isLoading
                            )
                        }
                        composable(
                            route = NavGraph.Main.editPassword,
                            arguments = listOf(
                                navArgument("id") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id")
                            val password = passwords.find { it.id == id }
                            PasswordFormScreen(
                                navController = navController,
                                password = password,
                                onSave = { updatedPassword ->
                                    passwordViewModel.updatePassword(updatedPassword)
                                    navController.navigateUp()
                                },
                                isLoading = isLoading
                            )
                        }
                        composable(NavGraph.Main.settings) {
                            var isDataLoaded by remember { mutableStateOf(false) }
                            
                            LaunchedEffect(Unit) {
                                settingsViewModel.loadSettings()
                                delay(100)
                                isDataLoaded = true
                            }
                            
                            if (isDataLoaded) {
                                SettingsScreen(
                                    navController = navController,
                                    settings = settings ?: UserSettings(autoLogoutMinutes = 60),
                                    onUpdateSettings = { updatedSettings ->
                                        settingsViewModel.updateSettings(updatedSettings)
                                    },
                                    onChangeMasterPassword = { oldPassword, newPassword ->
                                        settingsViewModel.changeMasterPassword(oldPassword, newPassword)
                                    },
                                    isLoading = settingsViewModel.isLoading.collectAsState().value
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 