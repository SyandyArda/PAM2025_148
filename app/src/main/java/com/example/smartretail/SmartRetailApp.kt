package com.example.smartretail

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.smartretail.util.NotificationHelper
import com.example.smartretail.worker.LowStockWorker
import com.example.smartretail.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application Class dengan Hilt dan WorkManager Configuration
 * SRS Section 2.5: WorkManager untuk Background Sync
 */
@HiltAndroidApp
class SmartRetailApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize notification channel (SRS Section 2.2 - Notifikasi Stok)
        NotificationHelper(this).createNotificationChannel()

        // Schedule periodic background sync (SRS REQ-6: 15 menit interval)
        scheduleSyncWork()
        
        // Schedule low stock check (SRS Section 2.2: Notifikasi Stok Rendah)
        scheduleLowStockCheck()
    }

    /**
     * Konfigurasi WorkManager untuk menggunakan HiltWorkerFactory
     * Ini memungkinkan dependency injection di Worker classes
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    /**
     * Schedule periodic sync work setiap 15 menit
     * SRS Section 5.1: Battery Efficiency - Doze Mode compliance
     */
    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Hanya jalan saat ada internet
            .setRequiresBatteryNotLow(true) // Respect battery saver
            .build()

        // 1. PERIODIC WORK (setiap 15 menit)
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES // Interval 15 menit (SRS REQ-6)
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Jangan duplikat jika sudah ada
            syncWorkRequest
        )

        // 2. ONE-TIME WORK (untuk testing - jalan langsung saat app start)
        val immediateSync = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInitialDelay(5, TimeUnit.SECONDS) // Delay 5 detik setelah app start
            .build()

        WorkManager.getInstance(this).enqueue(immediateSync)
        android.util.Log.d("SmartRetailApp", "Immediate sync scheduled (5 seconds)")
    }
    
    /**
     * Schedule daily low stock check
     * SRS Section 2.2: Notifikasi Stok Rendah
     */
    private fun scheduleLowStockCheck() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)  // Jangan drain battery
            .build()
        
        // Check setiap hari (24 jam)
        val lowStockCheckRequest = PeriodicWorkRequestBuilder<LowStockWorker>(
            1, TimeUnit.DAYS  // Daily check
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)  // Delay 1 jam setelah app install
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            LowStockWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            lowStockCheckRequest
        )
        
        android.util.Log.d("SmartRetailApp", "Low stock check scheduled (daily)")
    }
}