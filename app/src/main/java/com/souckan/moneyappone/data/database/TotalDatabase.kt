package com.souckan.moneyappone.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.souckan.moneyappone.data.database.dao.TotalDao
import com.souckan.moneyappone.data.database.entity.TotalEntity

@Database(entities = [TotalEntity::class], version = 1, exportSchema = false)
abstract class TotalDatabase:RoomDatabase() {

    abstract fun getTotalDao():TotalDao
}