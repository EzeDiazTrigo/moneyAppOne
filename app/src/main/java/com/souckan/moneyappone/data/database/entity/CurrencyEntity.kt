package com.souckan.moneyappone.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")

data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCurrency") val idCurrency:Int = 0,
    @ColumnInfo(name = "currencyName") val currencyName:String,
    @ColumnInfo(name = "dollarPrice") val dollarPrice:Float
){
    constructor() : this(0, "", 0.0F)
}