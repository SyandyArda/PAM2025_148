package com.example.smartretail.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("transactions")
    suspend fun uploadTransactions(@Body transactions: List<TransactionUpload>)
}