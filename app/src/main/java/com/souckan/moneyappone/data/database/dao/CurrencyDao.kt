package com.souckan.moneyappone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import java.util.Currency

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency_table")
    suspend fun getAllCurrencies(): List<CurrencyEntity>

    @Query("SELECT currencyName FROM currency_table")
    suspend fun getAllCurrenciesNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currency: CurrencyEntity)

    @Query("DELETE FROM currency_table")
    suspend fun deleteAllCurrencies()

    @Query("SELECT * FROM currency_table WHERE currencyName = :code LIMIT 1")
    suspend fun getCurrencyByCode(code: String): CurrencyEntity?


}