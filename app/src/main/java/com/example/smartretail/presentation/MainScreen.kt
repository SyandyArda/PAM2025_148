package com.example.smartretail.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartretail.presentation.navigation.Screen
import com.example.smartretail.presentation.product.ProductScreen
import com.example.smartretail.presentation.transaction.TransactionScreen
import com.example.smartretail.presentation.transaction.TransactionViewModel
import com.example.smartretail.presentation.history.HistoryViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    // ViewModels for badge counts
    val transactionViewModel: TransactionViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    
    val cartItems by transactionViewModel.cartState.collectAsState()
    val transactions by historyViewModel.transactionList.collectAsState()
    val unsyncedCount = transactions.count { it.status == "PENDING" }

    // Daftar Menu Bawah (Dashboard sebagai tab pertama - SRS Section 4.1.1)
    val items = listOf(
        Screen.Dashboard,
        Screen.Transaction,
        Screen.Product,
        Screen.History,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Ambil rute layar yang sedang aktif
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    // Show badge for Kasir (cart count) and History (unsynced count)
                                    when (screen.route) {
                                        Screen.Transaction.route -> {
                                            if (cartItems.isNotEmpty()) {
                                                Badge {
                                                    Text(cartItems.size.toString())
                                                }
                                            }
                                        }
                                        Screen.History.route -> {
                                            if (unsyncedCount > 0) {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.error
                                                ) {
                                                    Text(unsyncedCount.toString())
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title, fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Agar saat back tidak numpuk layar banyak-banyak
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // AREA KONTEN UTAMA
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route, // Dashboard sebagai layar pertama (SRS 4.1.1)
            modifier = Modifier.padding(innerPadding)
        ) {
            // 0. SCREEN DASHBOARD (BARU)
            composable(Screen.Dashboard.route) {
                com.example.smartretail.presentation.dashboard.DashboardScreen(
                    onNavigateToProduct = {
                        navController.navigate(Screen.Product.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTransaction = {
                        navController.navigate(Screen.Transaction.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 1. SCREEN KASIR
            composable(Screen.Transaction.route) {
                TransactionScreen()
            }

            // 2. SCREEN PRODUK
            composable(Screen.Product.route) {
                ProductScreen()
            }

            // 3. SCREEN RIWAYAT (YANG SUDAH DIUPDATE)
            composable(Screen.History.route) {
                com.example.smartretail.presentation.history.HistoryScreen(
                    onItemClick = { transId ->
                        // Navigasi ke Layar Detail membawa ID Transaksi
                        navController.navigate(Screen.Detail.createRoute(transId))
                    }
                )
            }

            // 4. SCREEN DETAIL ORDER (BARU)
            composable(
                route = Screen.Detail.route
            ) {
                com.example.smartretail.presentation.history.OrderDetailScreen(
                    onBackClick = {
                        // Kembali ke layar sebelumnya
                        navController.popBackStack()
                    }
                )
            }

            // 5. SCREEN PROFILE
            composable(Screen.Profile.route) {
                com.example.smartretail.presentation.profile.ProfileScreen()
            }
        }
    }
}