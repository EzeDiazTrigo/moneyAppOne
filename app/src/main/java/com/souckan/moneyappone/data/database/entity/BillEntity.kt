package com.souckan.moneyappone.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bill_table")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idBill") val idBill:Int = 0,
    @ColumnInfo(name = "idCurrency") val idCurrency:Int,
    @ColumnInfo(name = "idAccount") val idAccount: Int,
    @ColumnInfo(name = "amount") val amount:Float,
    @ColumnInfo(name = "billDate") val billDate:String,
    @ColumnInfo(name = "description") val description:String
){
    constructor() : this(0, 0, 0,0.0F,"","")
}

//fun Bill.toDatabase() = BillEntity(idCurrency = idCurrency, idAccount = idAccount, amount = amount, billDate = billDate, description = description)