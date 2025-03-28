package com.souckan.moneyappone.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.souckan.moneyappone.domain.model.Total

@Entity(tableName = "total_table")
data class TotalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTotal") val idTotal:Int = 0,
    @ColumnInfo(name = "currency") val currency:String,
    @ColumnInfo(name = "totalAmount") val totalAmount:Float,
    @ColumnInfo(name = "account") val account:String
)

fun Total.toDatabase() = TotalEntity(currency = currency, totalAmount = totalAmount, account = account)