package com.example.smartretail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartretail.data.local.Transaction
import kotlinx.coroutines.flow.Flow

// Class Helper untuk menampung hasil Join (Header + Detail)
data class OrderDetailItem(
    val productId: Int, 
    val name: String, // Nama Produk (dari tabel products)
    val price: Long,  // Harga Satuan (dari tabel products)
    val qty: Int,     // Jumlah Beli (dari tabel transaction_items)
    val subtotal: Long // Subtotal (dari tabel transaction_items)
)

// Data class untuk Produk Terlaris (SRS Section 2.2 - Laporan Penjualan)
data class BestSellingProduct(
    val name: String,
    val totalQty: Int,
    val totalRevenue: Long
)

// Data class untuk Revenue Trend Chart
data class DailyRevenue(
    val date: String,
    val revenue: Long
)

@Dao
interface TransactionDao {
    // 1. Simpan Header Transaksi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // 2. Simpan Banyak Item Sekaligus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    // 3. Fungsi Pembungkus (Atomic Operation)
    @androidx.room.Transaction
    suspend fun insertFullTransaction(
        transaction: Transaction,
        items: List<TransactionItem>
    ) {
        insertTransaction(transaction)
        insertTransactionItems(items)
    }

    // Ambil Transaksi yang BELUM disinkronkan (Untuk WorkManager)
    @Query("SELECT * FROM transactions WHERE status = 'PENDING'")
    suspend fun getUnsyncedTransactions(): List<Transaction>

    // Update Status jadi SYNCED kalau upload berhasil
    @Query("UPDATE transactions SET status = 'SYNCED' WHERE transaction_id = :transId")
    suspend fun markAsSynced(transId: String)

    // Ambil semua transaksi, urutkan dari yang terbaru (DESC)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // Ambil satu transaksi berdasarkan ID
    @Query("SELECT * FROM transactions WHERE transaction_id = :transId")
    suspend fun getTransactionById(transId: String): Transaction?

    // 3. Query JOIN: Ambil item transaksi + Nama Produknya
    @Query("""
        SELECT p.product_id AS productId, p.name, p.price, ti.qty, ti.subtotal
        FROM transaction_items ti
        INNER JOIN products p ON ti.product_id = p.product_id
        WHERE ti.transaction_id = :transId
    """)
    fun getTransactionItems(transId: String): Flow<List<OrderDetailItem>>

    // Dashboard Statistics (SRS Feature 2.2.5 - Visualisasi Omzet)
    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(total_price), 0) FROM transactions")
    fun getTotalRevenue(): Flow<Long>

    // Analytics: Today's Revenue (SRS Section 2.2 - Laporan Penjualan Harian)
    @Query("""
        SELECT COALESCE(SUM(total_price), 0) 
        FROM transactions 
        WHERE date BETWEEN :startOfDay AND :endOfDay
    """)
    fun getTodayRevenue(startOfDay: Long, endOfDay: Long): Flow<Long>

    // Analytics: Today's Transaction Count
    @Query("""
        SELECT COUNT(*) 
        FROM transactions 
        WHERE date BETWEEN :startOfDay AND :endOfDay
    """)
    fun getTodayTransactionCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    // Analytics: Best Selling Products (Top 5)
    @Query("""
        SELECT p.name, SUM(ti.qty) as totalQty, SUM(ti.subtotal) as totalRevenue
        FROM transaction_items ti
        INNER JOIN products p ON ti.product_id = p.product_id
        GROUP BY ti.product_id
        ORDER BY totalQty DESC
        LIMIT 5
    """)
    fun getBestSellingProducts(): Flow<List<BestSellingProduct>>

    // Weekly Revenue (Last 7 days) - For Chart
    @Query("""
        SELECT strftime('%Y-%m-%d', date/1000, 'unixepoch', 'localtime') as date,
               SUM(total_price) as revenue
        FROM transactions
        WHERE date >= :startDate
        GROUP BY strftime('%Y-%m-%d', date/1000, 'unixepoch', 'localtime')
        ORDER BY date ASC
    """)
    suspend fun getWeeklyRevenue(startDate: Long): List<DailyRevenue>

    // Monthly Revenue (Last 30 days) - For Chart
    @Query("""
        SELECT strftime('%Y-%m-%d', date/1000, 'unixepoch', 'localtime') as date,
               SUM(total_price) as revenue
        FROM transactions
        WHERE date >= :startDate
        GROUP BY strftime('%Y-%m-%d', date/1000, 'unixepoch', 'localtime')
        ORDER BY date ASC
    """)
    suspend fun getMonthlyRevenue(startDate: Long): List<DailyRevenue>

}