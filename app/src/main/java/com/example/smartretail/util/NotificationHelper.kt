package com.example.smartretail.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartretail.MainActivity
import com.example.smartretail.R

/**
 * Helper class untuk mengelola notifikasi Android
 * SRS Section 2.2: Notifikasi Stok Rendah
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "low_stock_channel"
        const val CHANNEL_NAME = "Low Stock Alerts"
        private const val NOTIFICATION_ID_BASE = 1000
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    /**
     * Create notification channel (required for Android O+)
     * Dipanggil saat app start di SmartRetailApp.onCreate()
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk produk dengan stok rendah"
                enableVibration(true)
                enableLights(true)
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
                as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Show low stock notification
     * @param productId ID produk untuk unique notification
     * @param productName Nama produk
     * @param stock Jumlah stok tersisa
     */
    fun showLowStockNotification(productId: Int, productName: String, stock: Int) {
        // Intent untuk membuka app saat notification di-tap
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // TODO: Add proper icon
            .setContentTitle("⚠️ Stok Rendah!")
            .setContentText("$productName tersisa $stock unit")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Produk \"$productName\" hanya tersisa $stock unit. Segera lakukan restock!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        // Show notification dengan unique ID per produk
        notificationManager.notify(NOTIFICATION_ID_BASE + productId, notification)
    }
    
    /**
     * Cancel notification untuk produk tertentu
     * Dipanggil saat stok sudah di-restock
     */
    fun cancelLowStockNotification(productId: Int) {
        notificationManager.cancel(NOTIFICATION_ID_BASE + productId)
    }
    
    /**
     * Cancel all low stock notifications
     */
    fun cancelAllLowStockNotifications() {
        notificationManager.cancelAll()
    }
}
