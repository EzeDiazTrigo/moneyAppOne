package com.souckan.moneyappone.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.souckan.moneyappone.data.database.entity.BillEntity
import androidx.room.Query
import com.souckan.moneyappone.domain.model.BillWithDetails

@Dao
interface BillDao {

    @Query("SELECT * FROM bill_table ORDER BY billDate DESC")
    suspend fun getAllBills(): List<BillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bill: BillEntity)

    @Query("DELETE FROM bill_table")
    suspend fun deleteAllBills()

    @Query("SELECT * FROM bill_table WHERE idAccount = :idAccount ORDER BY billDate DESC")
    suspend fun getAllBillByAccount(idAccount: Int): List<BillEntity>

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("UPDATE total_table SET totalAmount = totalAmount - :amount WHERE idAccount = :accountId")
    suspend fun subtractFromTotal(accountId: Int, amount: Float)

    @Query("SELECT * FROM bill_table WHERE idBill = :billId LIMIT 1")
    suspend fun getBillById(billId: Int): BillEntity?

    @Query("""
    SELECT b.idBill, b.amount, b.description, b.billDate, 
           a.accountName, c.currencyName 
    FROM bill_table b
    INNER JOIN account_table a ON b.idAccount = a.idAccount
    INNER JOIN currency_table c ON b.idCurrency = c.idCurrency ORDER BY billDate DESC
""")
    fun getAllBillsWithDetails(): LiveData<List<BillWithDetails>>

    @Query("""
    SELECT b.idBill, b.amount, b.description, b.billDate, a.accountName, c.currencyName
    FROM bill_table b
    INNER JOIN account_table a ON b.idAccount = a.idAccount
    INNER JOIN currency_table c ON b.idCurrency = c.idCurrency
    WHERE b.idAccount = :idAccount ORDER BY billDate DESC
""")
    fun getBillsByAccount(idAccount: Int): LiveData<List<BillWithDetails>>
}