package com.souckan.moneyappone.data.network.response

data class CryptoPriceResponse(
    val bitcoin: Bitcoin
)

data class Bitcoin(
    val usd: Double
)
