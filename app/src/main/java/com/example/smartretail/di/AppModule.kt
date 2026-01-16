package com.example.smartretail.di

import android.content.Context
import com.example.smartretail.BuildConfig
import com.example.smartretail.data.local.ProductDao
import com.example.smartretail.data.local.SmartRetailDatabase
import com.example.smartretail.data.local.TransactionDao
import com.example.smartretail.data.local.UserDao
import com.example.smartretail.data.local.datastore.UserPreferences
import com.example.smartretail.data.remote.ApiService
import com.example.smartretail.data.repository.AuthRepositoryImpl
import com.example.smartretail.data.repository.ProductRepositoryImpl
import com.example.smartretail.data.repository.TransactionRepositoryImpl
import com.example.smartretail.domain.repository.AuthRepository
import com.example.smartretail.domain.repository.ProductRepository
import com.example.smartretail.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- DataStore Preferences ---
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context) = UserPreferences(context)

    // --- Network (Retrofit) ---
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // --- Repositories ---
    @Provides
    @Singleton
    fun provideAuthRepository(userDao: UserDao, preferences: UserPreferences): AuthRepository {
        return AuthRepositoryImpl(userDao, preferences)
    }

    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepositoryImpl(productDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        productDao: ProductDao,
        db: SmartRetailDatabase,
        apiService: ApiService
    ): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao, productDao, db, apiService)
    }
}