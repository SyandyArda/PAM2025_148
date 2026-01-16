package com.example.smartretail.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartretail.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by authViewModel.currentUserName.collectAsState(initial = "User")
    val syncStatus by profileViewModel.syncStatus.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to password change events
    LaunchedEffect(Unit) {
        authViewModel.passwordChangeEvent.collect { event ->
            when (event) {
                is AuthViewModel.PasswordChangeEvent.Success -> {
                    showPasswordDialog = false
                    snackbarHostState.showSnackbar("Password berhasil diubah!")
                }
                is AuthViewModel.PasswordChangeEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // Refresh sync status setiap kali screen muncul
    LaunchedEffect(Unit) {
        profileViewModel.checkSyncStatus()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profil Pengguna",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Foto Profil Bulat
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama User
            Text(
                text = userName ?: "Kasir",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Staff Toko",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Change Password (SRS REQ-14)
            OutlinedButton(
                onClick = { showPasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ubah Password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kartu Status Aplikasi
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Status Aplikasi", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Database Status (Always Active)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Database Lokal: Aktif")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sync Status (Dynamic)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (syncStatus) {
                            is ProfileViewModel.SyncStatus.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Memeriksa status...")
                            }
                            is ProfileViewModel.SyncStatus.Synced -> {
                                Badge(containerColor = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sinkronisasi: Semua data ter-sync ✓")
                            }
                            is ProfileViewModel.SyncStatus.Pending -> {
                                val count = (syncStatus as ProfileViewModel.SyncStatus.Pending).count
                                Badge(containerColor = Color(0xFFFF9800))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sinkronisasi: $count transaksi menunggu sync")
                            }
                            is ProfileViewModel.SyncStatus.Error -> {
                                Badge(containerColor = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sinkronisasi: Error")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Theme Selection Card (SRS Section 4.1.2 - Dark Mode Support)
            ThemeSelectionCard()

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Logout
            Button(
                onClick = { authViewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT / KELUAR")
            }
        }
    }

    // Dialog Change Password
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPass, newPass ->
                // Hardcoded userId = 1 karena auto-seeding hanya buat 1 user
                authViewModel.changePassword(1, oldPass, newPass)
            }
        )
    }
}

@Composable
fun ThemeSelectionCard() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentTheme by com.example.smartretail.data.preferences.ThemePreferences.getTheme(context)
        .collectAsState(initial = com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tema Aplikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Light Theme Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.LIGHT,
                    onClick = {
                        scope.launch {
                            com.example.smartretail.data.preferences.ThemePreferences.saveTheme(
                                context,
                                com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.LIGHT
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Terang")
            }
            
            // Dark Theme Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.DARK,
                    onClick = {
                        scope.launch {
                            com.example.smartretail.data.preferences.ThemePreferences.saveTheme(
                                context,
                                com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.DARK
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gelap")
            }
            
            // System Theme Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.SYSTEM,
                    onClick = {
                        scope.launch {
                            com.example.smartretail.data.preferences.ThemePreferences.saveTheme(
                                context,
                                com.example.smartretail.data.preferences.ThemePreferences.ThemeMode.SYSTEM
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ikuti Sistem")
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Password") },
        text = {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Password Lama") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Password Baru") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Password Baru") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Semua field harus diisi"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Password baru tidak cocok"
                        }
                        newPassword.length < 6 -> {
                            errorMessage = "Password minimal 6 karakter"
                        }
                        else -> {
                            errorMessage = ""
                            onConfirm(oldPassword, newPassword)
                        }
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
