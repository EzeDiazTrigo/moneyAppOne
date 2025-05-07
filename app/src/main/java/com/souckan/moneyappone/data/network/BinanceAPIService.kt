package com.souckan.moneyappone.data.network


import com.souckan.moneyappone.data.network.response.BinancePriceResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface BinanceAPIService {
    @GET("api/v3/ticker/price")
    suspend fun getPrice(@Query("symbol") symbol: String): BinancePriceResponse
}


