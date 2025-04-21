package com.passmanager.ui.screens.passwords

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.passmanager.ui.R
import com.passmanager.ui.data.model.PasswordEntry
import com.passmanager.ui.viewmodels.PasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordListScreen(
    navController: NavController,
    passwords: List<PasswordEntry>,
    onDeletePassword: (Long) -> Unit,
    isLoading: Boolean,
    viewModel: PasswordViewModel
) {
    var showDeleteDialog by remember { mutableStateOf<PasswordEntry?>(null) }
    var showPasswordDialog by remember { mutableStateOf<PasswordEntry?>(null) }
    var masterPassword by remember { mutableStateOf("") }
    val decryptedPassword by viewModel.decryptedPassword.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.passwords)) },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_password") }
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Password")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (passwords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No passwords yet. Click + to add one.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(passwords) { password ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { showPasswordDialog = password },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = password.resourceName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = password.username,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = { navController.navigate("edit_password/${password.id}") }
                                ) {
                                    Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                                }
                                IconButton(
                                    onClick = { showDeleteDialog = password }
                                ) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showPasswordDialog?.let { password ->
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = null
                viewModel.clearDecryptedPassword()
            },
            title = { Text(password.resourceName) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Username: ${password.username}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (decryptedPassword != null) {
                        Text(
                            text = "Password: $decryptedPassword",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        OutlinedTextField(
                            value = masterPassword,
                            onValueChange = { masterPassword = it },
                            label = { Text("Master Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Button(
                            onClick = { viewModel.decryptPassword(password.id!!, masterPassword) },
                            enabled = masterPassword.isNotBlank() && !isLoading
                        ) {
                            Text("Decrypt Password")
                        }
                    }
                    
                    if (password.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${password.notes}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showPasswordDialog = null
                    viewModel.clearDecryptedPassword()
                }) {
                    Text("Close")
                }
            }
        )
    }

    showDeleteDialog?.let { password ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Password") },
            text = { Text("Are you sure you want to delete this password?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePassword(password.id!!)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}