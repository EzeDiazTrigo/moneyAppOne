package com.souckan.moneyappone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.souckan.moneyappone.data.database.entity.TotalEntity
import androidx.room.Query

@Dao
interface TotalDao {

    @Query("SELECT * FROM total_table ORDER BY idTotal DESC")
    suspend fun getAllTotals():List<TotalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(total: List<TotalEntity>)

    @Query("DELETE FROM total_table")
    suspend fun deleteAllTotals()
}