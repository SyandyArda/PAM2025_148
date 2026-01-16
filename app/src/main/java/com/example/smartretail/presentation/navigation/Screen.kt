package com.example.smartretail.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.AccountCircle

// Daftar Alamat Layar
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Transaction : Screen("transaction", "Kasir", Icons.Default.ShoppingCart)
    object Product : Screen("product", "Produk", Icons.Default.Inventory)
    // History kita siapkan tempatnya dulu
    object History : Screen("history", "Riwayat", Icons.Default.History)

    object Detail : Screen("order_detail/{transId}", "Detail", Icons.Default.History) {
        fun createRoute(transId: String) = "order_detail/$transId"
    }
    object Profile : Screen("profile", "Profil", Icons.Default.AccountCircle) // <-- Tambah ini
}