package com.example.smartretail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.smartretail.data.local.User
import com.example.smartretail.data.local.UserDao
import com.example.smartretail.data.preferences.ThemePreferences
import com.example.smartretail.domain.repository.AuthRepository
import com.example.smartretail.presentation.MainScreen
import com.example.smartretail.presentation.auth.LoginScreen
import com.example.smartretail.presentation.auth.RegisterScreen
import com.example.smartretail.presentation.splash.SplashScreen
import com.example.smartretail.ui.theme.SmartRetailTheme
import com.example.smartretail.util.PasswordHasher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val themeMode by ThemePreferences.getTheme(context).collectAsState(initial = ThemePreferences.ThemeMode.SYSTEM)
            
            // Determine dark theme based on user preference
            val darkTheme = when (themeMode) {
                ThemePreferences.ThemeMode.LIGHT -> false
                ThemePreferences.ThemeMode.DARK -> true
                ThemePreferences.ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            SmartRetailTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    var userCount by remember { mutableStateOf<Int?>(null) }
                    val sessionToken by authRepository.getSession().collectAsState(initial = "LOADING")
                    
                    // Check user count on first load
                    LaunchedEffect(Unit) {
                        userCount = userDao.getUserCount()
                        android.util.Log.d("MainActivity", "User Count: $userCount")
                        // Clear session if no users exist (database was cleared)
                        if (userCount == 0) {
                            android.util.Log.d("MainActivity", "Clearing session - no users")
                            authRepository.logout()
                        }
                    }
                    
                    // Debug logging
                    LaunchedEffect(showSplash, userCount, sessionToken) {
                        android.util.Log.d("MainActivity", "Navigation State - Splash: $showSplash, UserCount: $userCount, SessionToken: $sessionToken")
                    }

                    when {
                        showSplash -> {
                            // Show Splash Screen first
                            SplashScreen(
                                onNavigateToDashboard = {
                                    showSplash = false
                                }
                            )
                        }
                        userCount == null -> {
                            // Loading user count
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        userCount == 0 -> {
                            // First time - Show Register Screen (PRIORITY!)
                            RegisterScreen(
                                onRegisterSuccess = {
                                    // Refresh user count after registration
                                    lifecycleScope.launch {
                                        userCount = userDao.getUserCount()
                                    }
                                }
                            )
                        }
                        sessionToken == "LOADING" -> {
                            // Loading session check
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        sessionToken == null -> {
                            // Show Login if not authenticated
                            LoginScreen(
                                onLoginSuccess = {
                                    // Auto navigate via state change
                                }
                            )
                        }
                        else -> {
                            // Show Main Dashboard
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}