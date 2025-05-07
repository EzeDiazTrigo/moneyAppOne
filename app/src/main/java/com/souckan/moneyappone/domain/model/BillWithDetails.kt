package com.souckan.moneyappone.domain.model

data class BillWithDetails(
    val idBill: Int,
    val amount: Double,
    val description: String,
    val billDate: String,
    val accountName: String,   // En lugar de idAccount
    val currencyName: String   // En lugar de idCurrency
)