package com.example.smartretail.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartretail.domain.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Background sync started...")

            val unsyncedTransactions = transactionRepository.getUnsyncedTransactions()

            if (unsyncedTransactions.isEmpty()) {
                Log.d("SyncWorker", "No unsynced transactions found")
                return Result.success()
            }

            Log.d("SyncWorker", "Found ${unsyncedTransactions.size} unsynced transactions")

            // FIXED: Properly return the result from fold
            val uploadResult = transactionRepository.uploadTransactions(unsyncedTransactions)
            
            uploadResult.fold(
                onSuccess = {
                    unsyncedTransactions.forEach { transaction ->
                        transactionRepository.markAsSynced(transaction.transactionId)
                        Log.d("SyncWorker", "Transaction ${transaction.transactionId} synced successfully")
                    }
                    Log.d("SyncWorker", "Background sync completed successfully")
                },
                onFailure = {
                    Log.e("SyncWorker", "Sync failed: ${it.message}", it)
                }
            )
            
            // Return success if upload succeeded, retry if failed
            if (uploadResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_transactions_work"
    }
}