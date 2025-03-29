package com.souckan.moneyappone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.souckan.moneyappone.data.database.entity.AccountEntity

@Dao
interface AccountDao {
    @Query("SELECT * FROM account_table ORDER BY accountName DESC")
    suspend fun getAllAccount():List<AccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(account: AccountEntity)

    @Query("DELETE FROM account_table")
    suspend fun deleteAllAccounts()

    @Query("SELECT * FROM account_table WHERE accountName = :name LIMIT 1")
    suspend fun getAccountByName(name: String): AccountEntity?


}