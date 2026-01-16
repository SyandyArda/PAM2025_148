package com.example.smartretail.data.repository

import com.example.smartretail.data.local.User
import com.example.smartretail.data.local.UserDao
import com.example.smartretail.data.local.datastore.UserPreferences
import com.example.smartretail.domain.repository.AuthRepository
import com.example.smartretail.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferences: UserPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String): User? {
        // Ambil user dari database
        val user = userDao.getUserByUsername(username) ?: return null
        
        // Verify password menggunakan PasswordHasher (SHA-256 + Salt)
        return if (PasswordHasher.verifyPassword(password, user.passwordHash)) {
            user
        } else {
            null
        }
    }
    
    override suspend fun register(username: String, password: String, storeName: String): Result<User> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                return Result.failure(Exception("Username sudah digunakan"))
            }
            
            // Create new user with hashed password
            val newUser = User(
                username = username,
                passwordHash = PasswordHasher.hashPassword(password),
                storeName = storeName,
                createdAt = System.currentTimeMillis()
            )
            
            // Insert to database
            userDao.insertUser(newUser)
            
            // Return the created user
            val createdUser = userDao.getUserByUsername(username)
            if (createdUser != null) {
                Result.success(createdUser)
            } else {
                Result.failure(Exception("Gagal membuat user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveSession(user: User) {
        // Simpan ke DataStore
        preferences.saveSession(user.userId.toString(), user.username)
    }

    override suspend fun logout() {
        preferences.clearSession()
    }

    override fun getSession(): Flow<String?> {
        return preferences.getUserToken
    }

    override fun getUserName(): kotlinx.coroutines.flow.Flow<String?> {
        return preferences.getUserName
    }

    override suspend fun getCurrentUser(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    override suspend fun changePassword(
        userId: Int,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            // Ambil user saat ini
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("User tidak ditemukan"))

            // Verifikasi password lama menggunakan PasswordHasher
            if (!PasswordHasher.verifyPassword(oldPassword, user.passwordHash)) {
                return Result.failure(Exception("Password lama salah"))
            }

            // Hash password baru sebelum disimpan
            val hashedNewPassword = PasswordHasher.hashPassword(newPassword)
            userDao.updatePassword(userId, hashedNewPassword)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStoreName(userId: Int, storeName: String) {
        userDao.updateStoreName(userId, storeName)
    }
}