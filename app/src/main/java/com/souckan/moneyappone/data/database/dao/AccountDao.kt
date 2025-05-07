package com.souckan.moneyappone.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.souckan.moneyappone.data.database.entity.AccountEntity

@Dao
interface AccountDao {
    @Query("SELECT * FROM account_table ORDER BY accountName DESC")
    suspend fun getAllAccount(): List<AccountEntity>

    @Query("SELECT accountName FROM account_table ORDER BY accountName DESC")
    suspend fun getAllAccountNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(account: AccountEntity)

    @Query("DELETE FROM account_table")
    suspend fun deleteAllAccounts()

    @Query("SELECT * FROM account_table WHERE accountName = :accountName AND idCurrency = :idCurrency LIMIT 1")
    suspend fun getAccountByName(accountName: String, idCurrency: Int): AccountEntity?

    @Query("SELECT accountName FROM account_table WHERE idAccount = :id LIMIT 1")
    fun getAccountNameById(id: Int): LiveData<String>

    @Query("SELECT c.currencyName FROM account_table a JOIN currency_table c ON a.idCurrency = c.idCurrency WHERE a.idAccount = :accountId")
    suspend fun getCurrencyNameByAccountId(accountId:Int):String?

    @Query("DELETE FROM bill_table WHERE idAccount = :accountId")
    suspend fun deleteBillsByAccount(accountId: Int)

    @Query("DELETE FROM account_table WHERE idAccount = :accountId")
    suspend fun deleteAccount(accountId: Int)

    @Query("DELETE FROM total_table WHERE idAccount = :accountId")
    fun deleteTotalsByAccount(accountId: Int)

    @Transaction
    suspend fun deleteAccountWithBills(accountId: Int) {
        deleteBillsByAccount(accountId)
        deleteTotalsByAccount(accountId)
        deleteAccount(accountId)
    }

    @Query("UPDATE account_table SET accountName = :newName WHERE idAccount = :accountId")
    suspend fun updateAccountName(accountId: Int, newName: String)

}