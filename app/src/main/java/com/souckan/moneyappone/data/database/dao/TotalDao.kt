package com.souckan.moneyappone.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.souckan.moneyappone.data.database.entity.TotalEntity
import androidx.room.Query
import com.souckan.moneyappone.domain.model.TotalWithDetails

@Dao
interface TotalDao {

    @Query("SELECT * FROM total_table ORDER BY idAccount DESC")
    suspend fun getAllTotals(): List<TotalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(total: List<TotalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTotal(total: TotalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTotal(total: TotalEntity)

    @Query("DELETE FROM total_table")
    suspend fun deleteAllTotals()

    @Query("SELECT * FROM total_table WHERE idAccount = :account AND  idCurrency = :currency")
    suspend fun getTotalByAccountAndCurrency(account: Int, currency: Int): TotalEntity

    @Query("""
    SELECT SUM(t.totalAmount / c.dollarPrice) 
    FROM total_table t 
    INNER JOIN currency_table c ON t.idCurrency = c.idCurrency
""")
    fun getTotalSumInDollars(): LiveData<Double>

    @Query("""
    SELECT t.idAccount, a.accountName, t.totalAmount, c.currencyName 
    FROM total_table t
    INNER JOIN account_table a ON t.idAccount = a.idAccount
    INNER JOIN currency_table c ON a.idCurrency = c.idCurrency ORDER BY a.accountName ASC
""")
    fun getAllTotalsWithDetails(): LiveData<List<TotalWithDetails>>

    @Query("""
    SELECT SUM(t.totalAmount) 
    FROM total_table t 
    INNER JOIN currency_table c ON t.idCurrency = c.idCurrency 
    WHERE c.currencyName != :excludedARS AND c.currencyName != :excludedBTC AND c.currencyName != :excludedEUR 
""")
    fun getTotalNonARS(excludedARS: String = "ARS", excludedBTC: String = "BTC", excludedEUR: String = "EUR"): LiveData<Float?>

    @Query("""
    SELECT SUM(t.totalAmount) 
    FROM total_table t 
    INNER JOIN currency_table c ON t.idCurrency = c.idCurrency 
    WHERE c.currencyName = :currency
""")
    fun getTotalOnlyARS(currency: String = "ARS"): LiveData<Float?>

    @Query("""
    SELECT SUM(t.totalAmount) 
    FROM total_table t 
    INNER JOIN currency_table c ON t.idCurrency = c.idCurrency 
    WHERE c.currencyName = :currency
""")
    fun getTotalOnly(currency: String): LiveData<Float?>

}