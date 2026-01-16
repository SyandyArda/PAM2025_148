package com.example.smartretail.data.repository

import androidx.room.withTransaction
import com.example.smartretail.data.local.BestSellingProduct
import com.example.smartretail.data.local.DailyRevenue
import com.example.smartretail.data.local.OrderDetailItem
import com.example.smartretail.data.local.ProductDao
import com.example.smartretail.data.local.SmartRetailDatabase
import com.example.smartretail.data.local.Transaction
import com.example.smartretail.data.local.TransactionDao
import com.example.smartretail.data.local.TransactionItem
import com.example.smartretail.data.remote.ApiService
import com.example.smartretail.data.remote.TransactionItemUpload
import com.example.smartretail.data.remote.TransactionUpload
import com.example.smartretail.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val db: SmartRetailDatabase,
    private val apiService: ApiService
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: Transaction, items: List<TransactionItem>) {
        db.withTransaction {
            transactionDao.insertFullTransaction(transaction, items)
            items.forEach { item ->
                productDao.decreaseStock(item.productId, item.qty)
            }
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    override fun getTransactionItems(id: String): Flow<List<OrderDetailItem>> {
        return transactionDao.getTransactionItems(id)
    }

    override suspend fun getUnsyncedTransactions(): List<Transaction> {
        return transactionDao.getUnsyncedTransactions()
    }

    override suspend fun uploadTransactions(transactions: List<Transaction>): Result<Unit> {
        return try {
            // MOCK SIMULATION untuk testing (SRS REQ-8)
            // Dalam production, uncomment kode di bawah untuk real API call
            
            /* REAL API IMPLEMENTATION (uncomment untuk production):
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val uploadData = transactions.map { trans ->
                val items = transactionDao.getTransactionItems(trans.transactionId).first()
                TransactionUpload(
                    transactionId = trans.transactionId,
                    userId = trans.userId,
                    totalPrice = trans.totalPrice,
                    date = isoFormat.format(trans.date),
                    items = items.map { item ->
                        TransactionItemUpload(
                            productId = item.productId,
                            qty = item.qty,
                            subtotal = item.subtotal
                        )
                    }
                )
            }
            apiService.uploadTransactions(uploadData)
            */
            
            // MOCK: Simulasi delay network (500ms)
            kotlinx.coroutines.delay(500)
            
            // MOCK: Anggap upload selalu sukses
            android.util.Log.d("TransactionRepo", "Mock upload: ${transactions.size} transactions uploaded successfully")
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TransactionRepo", "Upload failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun markAsSynced(transactionId: String) {
        transactionDao.markAsSynced(transactionId)
    }

    override fun getTransactionCount(): Flow<Int> {
        return transactionDao.getTransactionCount()
    }

    override fun getTotalRevenue(): Flow<Long> {
        return transactionDao.getTotalRevenue()
    }

    override fun getTodayRevenue(startOfDay: Long, endOfDay: Long): Flow<Long> {
        return transactionDao.getTodayRevenue(startOfDay, endOfDay)
    }

    override fun getTodayTransactionCount(startOfDay: Long, endOfDay: Long): Flow<Int> {
        return transactionDao.getTodayTransactionCount(startOfDay, endOfDay)
    }

    override fun getBestSellingProducts(): Flow<List<BestSellingProduct>> {
        return transactionDao.getBestSellingProducts()
    }

    override suspend fun getWeeklyRevenue(): List<DailyRevenue> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.timeInMillis
        return transactionDao.getWeeklyRevenue(startDate)
    }

    override suspend fun getMonthlyRevenue(): List<DailyRevenue> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val startDate = calendar.timeInMillis
        return transactionDao.getMonthlyRevenue(startDate)
    }
}