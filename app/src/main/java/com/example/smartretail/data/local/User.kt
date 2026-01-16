package com.example.smartretail.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// SRS Section 7: Kamus Data - Users Table
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]  // Username harus unique
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0, // <--- PASTIKAN ADA "= 0" (Ini solusi garis merah di MainActivity)

    val username: String,
    val passwordHash: String,  // SHA-256 + Salt (SRS Section 5.3)
    val storeName: String,
    val createdAt: Long = System.currentTimeMillis() // SRS Kamus Data Section 7
)