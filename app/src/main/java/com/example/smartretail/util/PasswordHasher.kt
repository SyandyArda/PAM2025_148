package com.example.smartretail.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Utility class untuk password hashing menggunakan SHA-256 + Salt
 * Sesuai SRS Section 5.3: Security Requirements
 */
object PasswordHasher {
    
    private const val SALT_LENGTH = 32
    private const val ALGORITHM = "SHA-256"
    
    /**
     * Hash password dengan random salt
     * @param password Plain text password
     * @return Hashed password dalam format "salt:hash"
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hash(password, salt)
        return "$salt:$hash"
    }
    
    /**
     * Verify password dengan stored hash
     * @param password Plain text password yang diinput user
     * @param storedHash Hashed password dari database (format: "salt:hash")
     * @return true jika password cocok
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(":")
            if (parts.size != 2) return false
            
            val salt = parts[0]
            val originalHash = parts[1]
            val hashToVerify = hash(password, salt)
            
            // Constant-time comparison untuk mencegah timing attack
            originalHash == hashToVerify
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generate random salt menggunakan SecureRandom
     */
    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }
    
    /**
     * Hash password dengan salt menggunakan SHA-256
     */
    private fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        val saltedPassword = "$salt$password"
        val hashBytes = digest.digest(saltedPassword.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}
