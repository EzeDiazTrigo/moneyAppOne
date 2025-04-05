package com.souckan.moneyappone.data.network

import com.souckan.moneyappone.data.network.response.CryptoPriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CryptoAPIService {
    @GET("simple/price")
    suspend fun getBitcoinPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrencies: String = "usd"
    ): Response<CryptoPriceResponse>
}
