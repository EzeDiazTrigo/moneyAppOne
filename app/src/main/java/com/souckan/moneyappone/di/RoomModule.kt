package com.souckan.moneyappone.di

import android.content.Context
import androidx.room.Room
import com.souckan.moneyappone.data.database.TotalDatabase
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

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(context, TotalDatabase::class.java, TOTAL_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideTotalDao(db:TotalDatabase) = db.getTotalDao()

}