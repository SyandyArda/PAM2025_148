package com.example.smartretail.presentation.history

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.smartretail.presentation.product.toRupiah
import com.example.smartretail.util.PrintHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    onBackClick: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val transaction by viewModel.transaction.collectAsState()
    val items by viewModel.items.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Print Button (SRS Use Case: Cetak Struk)
                    if (transaction != null && items.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                PrintHelper.printReceipt(
                                    context = context,
                                    transaction = transaction!!,
                                    items = items,
                                    storeName = "Smart Retail Pusat"
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Cetak Struk"
                            )
                        }
                    }
                }
            )
        },
        // FAB untuk Share Receipt (SRS REQ-5)
        floatingActionButton = {
            if (transaction != null) {
                FloatingActionButton(
                    onClick = {
                        // Generate receipt text
                        val receiptText = viewModel.generateReceiptText("SmartRetail")

                        // Create share intent (SRS REQ-5: Digital proof via share intent)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, receiptText)
                            putExtra(Intent.EXTRA_SUBJECT, "Struk Transaksi SmartRetail")
                        }

                        // Launch share chooser
                        context.startActivity(
                            Intent.createChooser(shareIntent, "Bagikan Struk via")
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Bagikan Struk")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // HEADER INFO
            transaction?.let { trans ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order ID: #${trans.transactionId.take(8)}", fontWeight = FontWeight.Bold)
                        Text("Tanggal: ${formatDate(trans.date)}")
                        
                        // Status Badge dengan warna (SRS: PENDING/SYNCED)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Status: ", fontSize = 14.sp)
                            Badge(
                                containerColor = when (trans.status) {
                                    "SYNCED" -> Color(0xFF4CAF50) // Hijau
                                    "PENDING" -> Color(0xFFFF9800) // Orange
                                    else -> Color.Gray
                                }
                            ) {
                                Text(
                                    text = trans.status,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Total: ${toRupiah(trans.totalPrice)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Daftar Barang:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // LIST BARANG
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    Card(elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("${item.qty} x ${toRupiah(item.price)}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(toRupiah(item.subtotal), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}