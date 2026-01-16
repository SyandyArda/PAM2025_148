package com.example.smartretail.domain.repository

import com.example.smartretail.data.local.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Fungsi untuk UI: "Tolong kasih saya list produk yang update realtime"
    fun getProducts(): Flow<List<Product>>

    // Fungsi untuk UI: "Tolong simpan produk baru ini"
    suspend fun insertProduct(name: String, price: Long, stock: Int)

    // Fungsi untuk UI: "Tolong hapus produk ini"
    suspend fun deleteProduct(productId: Int)
    suspend fun updateProduct(product: Product)

    fun getProductCount(): kotlinx.coroutines.flow.Flow<Int>

    // Low Stock Detection (SRS Section 2.2 - Notifikasi Stok)
    fun getLowStockProducts(threshold: Int = 10): Flow<List<Product>>
    fun getLowStockCount(threshold: Int = 10): Flow<Int>
}