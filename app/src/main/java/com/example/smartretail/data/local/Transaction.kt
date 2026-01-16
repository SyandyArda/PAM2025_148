package com.example.smartretail.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "transactions",
    // Menghubungkan Transaksi dengan Kasir (User)
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.NO_ACTION // Data user tidak boleh dihapus sembarangan kalau ada transaksi
        )
    ],
    // Mempercepat pencarian berdasarkan user_id
    indices = [Index(value = ["userId"])]
)
data class Transaction(
    // PENTING: Gunakan UUID (String) bukan Int Auto Increment
    // Agar unik saat sinkronisasi banyak device
    @PrimaryKey
    @ColumnInfo(name = "transaction_id")
    val transactionId: String,

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "total_price")
    val totalPrice: Long,

    @ColumnInfo(name = "date")
    val date: Date, // Nanti dikonversi otomatis oleh Converters

    @ColumnInfo(name = "status")
    val status: String = "PENDING" // PENDING, SYNCED, FAILED
)