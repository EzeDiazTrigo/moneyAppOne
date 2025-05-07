package com.souckan.moneyappone.di

import android.content.Context
import androidx.room.Room
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.data.database.dao.AccountDao
import com.souckan.moneyappone.data.database.dao.BillDao
import com.souckan.moneyappone.data.database.dao.CurrencyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private val TOTAL_DATABASE_NAME = "total_database"
    
    @Volatile
    private var databaseInstance: TotalDatabase? = null

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TotalDatabase {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(
                context,
                TotalDatabase::class.java,
                "total_database"
            ).build()
        }
        return databaseInstance!!
    }

    fun closeDatabase() {
        databaseInstance?.close()
        databaseInstance = null
    }

    @Singleton
    @Provides
    fun provideTotalDao(db: TotalDatabase) = db.getTotalDao()

    @Singleton
    @Provides
    fun provideAccountDao(db: TotalDatabase): AccountDao = db.getAccountDao()

    @Singleton
    @Provides
    fun provideBillDao(db: TotalDatabase): BillDao = db.getBillDao()

    @Singleton
    @Provides
    fun provideCurrencyDao(db: TotalDatabase): CurrencyDao = db.getCurrencyDao()
}