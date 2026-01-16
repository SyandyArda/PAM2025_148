package com.example.smartretail.data.remote

import com.google.gson.annotations.SerializedName

// Data class untuk upload transaksi
data class TransactionUpload(
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("total_price") val totalPrice: Long,
    @SerializedName("date") val date: String, // Kirim sebagai ISO-8601 String
    @SerializedName("items") val items: List<TransactionItemUpload>
)

data class TransactionItemUpload(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("qty") val qty: Int,
    @SerializedName("subtotal") val subtotal: Long
)

// Data class untuk response dari server
data class UploadResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)
