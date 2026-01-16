package com.example.smartretail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Login Query (deprecated - use getUserByUsername + PasswordHasher)
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :password")
    suspend fun login(username: String, password: String): User?
    
    // Get User by Username (untuk password verification)
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    // Hitung User (Pastikan FROM users, bukan User)
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    // Insert User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Get User by ID (untuk change password & edit store)
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Int): User?

    // Update Password (SRS REQ-14 - Security)
    @Query("UPDATE users SET passwordHash = :newPasswordHash WHERE userId = :userId")
    suspend fun updatePassword(userId: Int, newPasswordHash: String)

    // Update Store Name
    @Query("UPDATE users SET storeName = :storeName WHERE userId = :userId")
    suspend fun updateStoreName(userId: Int, storeName: String)
}