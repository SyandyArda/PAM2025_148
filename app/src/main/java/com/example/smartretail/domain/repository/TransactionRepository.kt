package com.example.smartretail.domain.repository

import com.example.smartretail.data.local.BestSellingProduct
import com.example.smartretail.data.local.DailyRevenue
import com.example.smartretail.data.local.OrderDetailItem
import com.example.smartretail.data.local.Transaction
import com.example.smartretail.data.local.TransactionItem

interface TransactionRepository {
    // Fungsi untuk menyimpan Transaksi Lengkap (Header + Isi Keranjang)
    suspend fun saveTransaction(transaction: Transaction, items: List<TransactionItem>)
    fun getAllTransactions(): kotlinx.coroutines.flow.Flow<List<Transaction>>

    suspend fun getTransactionById(id: String): Transaction?
    fun getTransactionItems(id: String): kotlinx.coroutines.flow.Flow<List<OrderDetailItem>>

    // --- Sync Worker Functions ---
    suspend fun getUnsyncedTransactions(): List<Transaction>
    suspend fun uploadTransactions(transactions: List<Transaction>): Result<Unit>
    suspend fun markAsSynced(transactionId: String)

    // Dashboard Statistics (SRS Feature 2.2.5)
    fun getTransactionCount(): kotlinx.coroutines.flow.Flow<Int>
    fun getTotalRevenue(): kotlinx.coroutines.flow.Flow<Long>

    // Analytics (SRS Section 2.2 - Laporan Penjualan)
    fun getTodayRevenue(startOfDay: Long, endOfDay: Long): kotlinx.coroutines.flow.Flow<Long>
    fun getTodayTransactionCount(startOfDay: Long, endOfDay: Long): kotlinx.coroutines.flow.Flow<Int>
    fun getBestSellingProducts(): kotlinx.coroutines.flow.Flow<List<BestSellingProduct>>
    // Revenue Trends for Charts
    suspend fun getWeeklyRevenue(): List<DailyRevenue>
    suspend fun getMonthlyRevenue(): List<DailyRevenue>
}