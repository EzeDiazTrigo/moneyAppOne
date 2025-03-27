package com.souckan.moneyappone.data.network

import com.souckan.moneyappone.data.network.response.DollarResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface DolarAPIService {
    @GET
    suspend fun getDolarPrice(@Url url:String): Response<DollarResponse>
}