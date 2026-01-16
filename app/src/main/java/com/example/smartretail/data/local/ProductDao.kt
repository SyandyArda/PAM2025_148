package com.example.smartretail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Insert Barang Baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Ambil Semua Barang (Hanya yang belum dihapus / Soft Delete)
    // Return Flow agar UI update otomatis kalau data berubah (Real-time local)
    @Query("SELECT * FROM products WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Update Stok/Harga
    @Update
    suspend fun updateProduct(product: Product)

    // Soft Delete: Jangan hapus barisnya, tapi tandai is_deleted = 1
    // Ini agar saat sinkronisasi, server tahu barang ini dihapus
    @Query("UPDATE products SET is_deleted = 1, is_synced = 0 WHERE product_id = :productId")
    suspend fun softDeleteProduct(productId: Int)

    // Fungsi mengurangi stok secara atomik (Langsung di SQL biar aman)
    @Query("UPDATE products SET stock = stock - :qty WHERE product_id = :productId")
    suspend fun decreaseStock(productId: Int, qty: Int)

    // Dashboard: Hitung jumlah produk aktif (SRS Feature 2.2.5)
    @Query("SELECT COUNT(*) FROM products WHERE is_deleted = 0")
    fun getProductCount(): Flow<Int>

    // Low Stock Alert: Ambil produk dengan stok rendah (SRS Section 2.2 - Notifikasi Stok)
    @Query("SELECT * FROM products WHERE is_deleted = 0 AND stock < :threshold ORDER BY stock ASC")
    fun getLowStockProducts(threshold: Int = 10): Flow<List<Product>>

    // Low Stock Count: Hitung jumlah produk stok rendah untuk badge
    @Query("SELECT COUNT(*) FROM products WHERE is_deleted = 0 AND stock < :threshold")
    fun getLowStockCount(threshold: Int = 10): Flow<Int>
}