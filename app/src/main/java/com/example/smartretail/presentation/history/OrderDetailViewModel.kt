package com.example.smartretail.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.data.local.OrderDetailItem
import com.example.smartretail.data.local.Transaction
import com.example.smartretail.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle // Ini alat buat nangkep data lemparan dari layar sebelumnya
) : ViewModel() {

    // Ambil ID dari Navigasi
    private val transId: String = checkNotNull(savedStateHandle["transId"])

    // State untuk Header (Info Transaksi)
    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction = _transaction.asStateFlow()

    // State untuk List Item (Barang yang dibeli)
    private val _items = MutableStateFlow<List<OrderDetailItem>>(emptyList())
    val items = _items.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Ambil Header
            _transaction.value = repository.getTransactionById(transId)

            // Ambil List Barang
            repository.getTransactionItems(transId).collect {
                _items.value = it
            }
        }
    }

    /**
     * Generate formatted receipt text untuk sharing (SRS REQ-5)
     * Format: Plain text dengan Rupiah formatting (SRS Section 6.1)
     */
    fun generateReceiptText(storeName: String = "SmartRetail"): String {
        val trans = _transaction.value ?: return "Data transaksi tidak tersedia"
        val itemsList = _items.value

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
        val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        return buildString {
            appendLine("═══════════════════════════")
            appendLine("      $storeName")
            appendLine("═══════════════════════════")
            appendLine()
            appendLine("ID Transaksi: ${trans.transactionId}")
            appendLine("Tanggal: ${dateFormat.format(trans.date)}")
            appendLine("Status: ${trans.status}")
            appendLine()
            appendLine("───────────────────────────")
            appendLine("DETAIL PEMBELIAN")
            appendLine("───────────────────────────")

            itemsList.forEach { item ->
                appendLine("${item.name}")
                appendLine("  ${item.qty} x ${rupiahFormat.format(item.price)} = ${rupiahFormat.format(item.subtotal)}")
            }

            appendLine("───────────────────────────")
            appendLine("TOTAL: ${rupiahFormat.format(trans.totalPrice)}")
            appendLine("═══════════════════════════")
            appendLine()
            appendLine("Terima kasih atas kunjungan Anda!")
            appendLine("Powered by SmartRetail")
        }
    }
}