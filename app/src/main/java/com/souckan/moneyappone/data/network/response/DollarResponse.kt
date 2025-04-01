package com.souckan.moneyappone.data.network.response

data class DollarResponse(
    val moneda: String,
    val casa: String,
    val nombre: String,
    val compra: Int,
    val venta: Int,
    val fechaActualizacion: String
)