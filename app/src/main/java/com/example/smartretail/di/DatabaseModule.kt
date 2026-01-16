package com.example.smartretail.di

import android.content.Context
import androidx.room.Room
import com.example.smartretail.data.local.ProductDao
import com.example.smartretail.data.local.SmartRetailDatabase
import com.example.smartretail.data.local.TransactionDao
import com.example.smartretail.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartRetailDatabase {
        return Room.databaseBuilder(
            context,
            SmartRetailDatabase::class.java,
            "smart_retail.db"
        )
            .addMigrations(com.example.smartretail.data.local.MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: SmartRetailDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideProductDao(db: SmartRetailDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideTransactionDao(db: SmartRetailDatabase): TransactionDao = db.transactionDao()
}
