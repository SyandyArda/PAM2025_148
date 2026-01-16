package com.example.smartretail.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // <--- Ini Tanda Pengenal ke Hilt
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository // Inject Repository yang tadi kita bind
) : ViewModel() {

    // DATA STATE:
    // Mengambil Flow dari Repository dan mengubahnya jadi StateFlow.
    // UI nanti tinggal "collect" variabel ini. Kalau database berubah, ini ikut berubah.
    val productList = repository.getProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Hemat resource kalau aplikasi di-minimize
            initialValue = emptyList()
        )

    // USER ACTIONS:
    // Fungsi yang dipanggil saat tombol "Simpan" ditekan
    fun addProduct(name: String, price: String, stock: String) {
        viewModelScope.launch {
            // Konversi String inputan user ke tipe data yang benar
            val priceLong = price.toLongOrNull() ?: 0L
            val stockInt = stock.toIntOrNull() ?: 0

            if (name.isNotEmpty()) {
                repository.insertProduct(name, priceLong, stockInt)
            }
        }
    }

    // Fungsi saat user swipe delete
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    // FUNGSI BARU: Update Produk
    fun updateProduct(id: Int, name: String, price: String, stock: String) {
        viewModelScope.launch {
            val priceLong = price.toLongOrNull() ?: 0L
            val stockInt = stock.toIntOrNull() ?: 0

            // Buat object product baru dengan ID yang sama (agar menimpa data lama)
            val updatedProduct = com.example.smartretail.data.local.Product(
                productId = id, // ID tidak boleh berubah
                name = name,
                price = priceLong,
                stock = stockInt,
                isSynced = false // Tandai belum sync karena ada perubahan
            )
            repository.updateProduct(updatedProduct)
        }
    }
}