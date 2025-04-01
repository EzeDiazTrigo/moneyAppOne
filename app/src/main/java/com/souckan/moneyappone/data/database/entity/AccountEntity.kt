package com.souckan.moneyappone.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Currency

@Entity(tableName = "account_table")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idAccount") val idAccount: Int = 0,
    @ColumnInfo(name = "accountName") val accountName: String,
    @ColumnInfo(name = "idCurrency") val idCurrency: Int
) {
    constructor() : this(0, "", 0)
}