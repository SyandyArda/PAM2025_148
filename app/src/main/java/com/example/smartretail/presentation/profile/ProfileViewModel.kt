package com.example.smartretail.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Loading)
    val syncStatus = _syncStatus.asStateFlow()

    init {
        checkSyncStatus()
    }

    fun checkSyncStatus() {
        viewModelScope.launch {
            try {
                val unsyncedCount = transactionRepository.getUnsyncedTransactions().size
                _syncStatus.value = if (unsyncedCount > 0) {
                    SyncStatus.Pending(unsyncedCount)
                } else {
                    SyncStatus.Synced
                }
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class SyncStatus {
        object Loading : SyncStatus()
        object Synced : SyncStatus()
        data class Pending(val count: Int) : SyncStatus()
        data class Error(val message: String) : SyncStatus()
    }
}
