package com.example.smartretail.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.data.local.Product
import com.example.smartretail.data.local.Transaction
import com.example.smartretail.data.local.TransactionItem
import com.example.smartretail.domain.repository.ProductRepository
import com.example.smartretail.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

// Model sederhana untuk Keranjang Belanja UI
data class CartItemUi(
    val product: Product,
    val qty: Int
) {
    val subtotal: Long get() = product.price * qty
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // List Produk untuk dipilih
    val productList = productRepository.getProducts()

    // State Keranjang Belanja (Cart)
    private val _cartState = MutableStateFlow<List<CartItemUi>>(emptyList())
    val cartState: StateFlow<List<CartItemUi>> = _cartState.asStateFlow()

    // Hitung Total Belanja Realtime
    val totalTransaction: Long
        get() = _cartState.value.sumOf { it.subtotal }

    // VALIDASI & LOGIC: Tambah ke Keranjang
    fun addToCart(product: Product, qtyInput: String): String? {
        val qty = qtyInput.toIntOrNull()

        // 1. Validasi Input Angka
        if (qty == null || qty <= 0) {
            return "Jumlah tidak valid!"
        }

        // 2. Validasi Stok
        val currentInCart = _cartState.value.find { it.product.productId == product.productId }?.qty ?: 0
        val totalQtyReq = currentInCart + qty

        if (totalQtyReq > product.stock) {
            return "Stok tidak cukup! Sisa: ${product.stock}"
        }

        // Update Keranjang
        val currentCart = _cartState.value.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.product.productId == product.productId }

        if (existingItemIndex != -1) {
            // Kalau sudah ada, update qty
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(qty = existingItem.qty + qty)
        } else {
            // Kalau belum ada, tambah baru
            currentCart.add(CartItemUi(product, qty))
        }

        _cartState.value = currentCart
        return null // Null artinya sukses, tidak ada error
    }

    // Hapus item dari keranjang
    fun removeFromCart(product: Product) {
        _cartState.value = _cartState.value.filter { it.product.productId != product.productId }
    }
    
    // Update quantity item di keranjang
    fun updateCartItemQuantity(product: Product, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(product)
            return
        }
        
        if (newQty > product.stock) {
            return // Tidak bisa melebihi stok
        }
        
        val currentCart = _cartState.value.toMutableList()
        val index = currentCart.indexOfFirst { it.product.productId == product.productId }
        
        if (index != -1) {
            currentCart[index] = currentCart[index].copy(qty = newQty)
            _cartState.value = currentCart
        }
    }

    // Checkout: Simpan ke Database
    fun checkout(userId: Int = 1) { // Default user 1 dulu
        viewModelScope.launch {
            if (_cartState.value.isEmpty()) return@launch

            val transId = UUID.randomUUID().toString()
            val timestamp = Date()
            val total = totalTransaction

            // 1. Buat Header
            val transaction = Transaction(
                transactionId = transId,
                userId = userId,
                totalPrice = total,
                date = timestamp,
                status = "PENDING"
            )

            // 2. Buat Detail Items
            val items = _cartState.value.map { cartUi ->
                TransactionItem(
                    transactionId = transId,
                    productId = cartUi.product.productId,
                    qty = cartUi.qty,
                    subtotal = cartUi.subtotal
                )
                // Note: Stok Product idealnya dikurangin di sini juga via Repository
                // Tapi untuk tahap ini kita simpan transaksi dulu.
            }

            // 3. Simpan
            transactionRepository.saveTransaction(transaction, items)

            // 4. Reset Keranjang
            _cartState.value = emptyList()
        }
    }
}