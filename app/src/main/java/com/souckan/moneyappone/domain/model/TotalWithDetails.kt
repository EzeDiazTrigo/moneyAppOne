package com.souckan.moneyappone.domain.model

data class TotalWithDetails(
    val idAccount: Int,
    val accountName: String,
    val totalAmount: Double,
    val currencyName: String
)
