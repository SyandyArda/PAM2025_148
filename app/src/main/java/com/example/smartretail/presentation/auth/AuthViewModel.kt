package com.example.smartretail.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Channel untuk mengirim "Event" sekali tembak ke UI (misal: "Login Sukses" atau "Gagal")
    private val _loginEvent = Channel<LoginEvent>()
    val loginEvent = _loginEvent.receiveAsFlow()
    
    private val _registerEvent = Channel<RegisterEvent>()
    val registerEvent = _registerEvent.receiveAsFlow()

    fun login(user: String, pass: String) {
        viewModelScope.launch {
            if (user.isBlank() || pass.isBlank()) {
                _loginEvent.send(LoginEvent.Error("Username/Password tidak boleh kosong"))
                return@launch
            }

            // Cek ke Database
            val userAccount = repository.login(user, pass)

            if (userAccount != null) {
                // Login Berhasil -> Simpan Sesi
                repository.saveSession(userAccount)
                _loginEvent.send(LoginEvent.Success)
            } else {
                // Login Gagal
                _loginEvent.send(LoginEvent.Error("Username atau Password salah!"))
            }
        }
    }
    
    fun register(username: String, password: String, storeName: String) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank() || storeName.isBlank()) {
                _registerEvent.send(RegisterEvent.Error("Semua field harus diisi!"))
                return@launch
            }
            
            if (password.length < 6) {
                _registerEvent.send(RegisterEvent.Error("Password minimal 6 karakter"))
                return@launch
            }

            try {
                // Register user via repository
                val result = repository.register(username, password, storeName)
                
                if (result.isSuccess) {
                    // Auto login after registration
                    val userAccount = repository.login(username, password)
                    if (userAccount != null) {
                        repository.saveSession(userAccount)
                        _registerEvent.send(RegisterEvent.Success)
                    } else {
                        _registerEvent.send(RegisterEvent.Error("Registrasi berhasil, silakan login"))
                    }
                } else {
                    _registerEvent.send(RegisterEvent.Error(result.exceptionOrNull()?.message ?: "Gagal registrasi"))
                }
            } catch (e: Exception) {
                _registerEvent.send(RegisterEvent.Error(e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    // Ambil Nama User untuk ditampilkan di Profil
    val currentUserName = repository.getUserName()

    // Fungsi Logout
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            // Tidak perlu kirim event navigasi.
            // MainActivity memantau DataStore, begitu kosong -> Otomatis pindah LoginScreen.
        }
    }

    // Change Password (SRS REQ-14)
    private val _passwordChangeEvent = Channel<PasswordChangeEvent>()
    val passwordChangeEvent = _passwordChangeEvent.receiveAsFlow()

    fun changePassword(userId: Int, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            if (oldPassword.isBlank() || newPassword.isBlank()) {
                _passwordChangeEvent.send(PasswordChangeEvent.Error("Password tidak boleh kosong"))
                return@launch
            }

            if (newPassword.length < 6) {
                _passwordChangeEvent.send(PasswordChangeEvent.Error("Password minimal 6 karakter"))
                return@launch
            }

            val result = repository.changePassword(userId, oldPassword, newPassword)
            if (result.isSuccess) {
                _passwordChangeEvent.send(PasswordChangeEvent.Success)
            } else {
                _passwordChangeEvent.send(PasswordChangeEvent.Error(result.exceptionOrNull()?.message ?: "Gagal mengubah password"))
            }
        }
    }

    fun updateStoreName(userId: Int, storeName: String) {
        viewModelScope.launch {
            repository.updateStoreName(userId, storeName)
        }
    }

    // Definisi Event
    sealed class LoginEvent {
        object Success : LoginEvent()
        data class Error(val message: String) : LoginEvent()
    }
    
    sealed class RegisterEvent {
        object Success : RegisterEvent()
        data class Error(val message: String) : RegisterEvent()
    }

    sealed class PasswordChangeEvent {
        object Success : PasswordChangeEvent()
        data class Error(val message: String) : PasswordChangeEvent()
    }
}
