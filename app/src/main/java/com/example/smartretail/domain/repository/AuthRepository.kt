package com.example.smartretail.domain.repository

import com.example.smartretail.data.local.User

interface AuthRepository {
    // Login: Validasi username & password
    suspend fun login(username: String, password: String): User?

    // Register: Create new user account
    suspend fun register(username: String, password: String, storeName: String): Result<User>

    suspend fun saveSession(user: User)
    suspend fun logout()
    fun getSession(): kotlinx.coroutines.flow.Flow<String?> // Cek token
    fun getUserName(): kotlinx.coroutines.flow.Flow<String?>

    // Change Password & Store Settings (SRS REQ-14)
    suspend fun changePassword(userId: Int, oldPassword: String, newPassword: String): Result<Unit>
    suspend fun updateStoreName(userId: Int, storeName: String)
    suspend fun getCurrentUser(userId: Int): User?
}