package com.example.smartretail.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartretail.domain.repository.ProductRepository
import com.example.smartretail.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Background worker untuk cek stok rendah dan trigger notifikasi
 * SRS Section 2.2: Notifikasi Stok Rendah
 * 
 * Dijadwalkan untuk berjalan setiap hari (daily check)
 */
@HiltWorker
class LowStockWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val productRepository: ProductRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "low_stock_check_work"
        private const val LOW_STOCK_THRESHOLD = 10
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d("LowStockWorker", "Starting low stock check...")
            
            // Get low stock products dari repository
            val lowStockProducts = productRepository.getLowStockProducts(LOW_STOCK_THRESHOLD)
                .first()  // Get current value from Flow
            
            if (lowStockProducts.isEmpty()) {
                Log.d("LowStockWorker", "No low stock products found")
                return Result.success()
            }
            
            Log.d("LowStockWorker", "Found ${lowStockProducts.size} low stock products")
            
            // Show notification untuk setiap produk stok rendah
            val notificationHelper = NotificationHelper(applicationContext)
            lowStockProducts.forEach { product ->
                notificationHelper.showLowStockNotification(
                    productId = product.productId,
                    productName = product.name,
                    stock = product.stock
                )
                Log.d("LowStockWorker", "Notification sent for: ${product.name} (stock: ${product.stock})")
            }
            
            Log.d("LowStockWorker", "Low stock check completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            Log.e("LowStockWorker", "Low stock check failed: ${e.message}", e)
            Result.retry()
        }
    }
}
