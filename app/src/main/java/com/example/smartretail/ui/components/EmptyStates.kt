package com.example.smartretail.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartretail.ui.theme.Spacing

/**
 * Empty state untuk ProductScreen
 */
@Composable
fun EmptyProductState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        icon = Icons.Default.Inventory,
        title = "Belum Ada Produk",
        description = "Tambahkan produk pertama Anda untuk memulai",
        actionText = "Tambah Produk",
        onActionClick = onAddClick,
        modifier = modifier
    )
}

/**
 * Empty state untuk HistoryScreen
 */
@Composable
fun EmptyHistoryState(modifier: Modifier = Modifier) {
    EmptyStateTemplate(
        icon = Icons.Default.Receipt,
        title = "Belum Ada Transaksi",
        description = "Transaksi akan muncul di sini setelah checkout",
        modifier = modifier
    )
}

/**
 * Empty state untuk cart (TransactionScreen)
 */
@Composable
fun EmptyCartState(modifier: Modifier = Modifier) {
    EmptyStateTemplate(
        icon = Icons.Default.ShoppingCart,
        title = "Keranjang Kosong",
        description = "Pilih produk untuk memulai transaksi",
        modifier = modifier
    )
}

/**
 * Reusable empty state template
 */
@Composable
private fun EmptyStateTemplate(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        // Optional action button
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            Button(
                onClick = onActionClick,
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(Spacing.sm))
                Text(actionText)
            }
        }
    }
}
