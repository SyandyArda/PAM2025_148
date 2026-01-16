package com.example.smartretail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Daftarkan semua tabel di sini
@Database(
    entities = [
        User::class,
        Product::class,
        Transaction::class,
        TransactionItem::class
    ],
    version = 3, // Update ke versi 3 untuk menambah createdAt
    exportSchema = false
)
// Daftarkan Converter Tanggal
@TypeConverters(Converters::class)
abstract class SmartRetailDatabase : RoomDatabase() {
    // Daftarkan DAO agar bisa diakses
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
}

// Migration dari versi 2 ke 3: Tambah kolom created_at di tabel users
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Tambah kolom created_at dengan default value timestamp saat ini
        database.execSQL(
            "ALTER TABLE users ADD COLUMN createdAt INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))"
        )
    }
}