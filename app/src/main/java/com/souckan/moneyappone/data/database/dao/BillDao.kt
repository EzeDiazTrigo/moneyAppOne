package com.souckan.moneyappone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.souckan.moneyappone.data.database.entity.BillEntity
import androidx.room.Query

@Dao
interface BillDao {

    @Query("SELECT * FROM bill_table ORDER BY billDate DESC")
    suspend fun getAllBills():List<BillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bill: BillEntity)

    @Query("DELETE FROM bill_table")
    suspend fun deleteAllBills()

    @Query("SELECT * FROM bill_table WHERE idAccount = :idAccount ORDER BY billDate DESC")
    suspend fun getAllBillByAccount(idAccount:Int):List<BillEntity>

}