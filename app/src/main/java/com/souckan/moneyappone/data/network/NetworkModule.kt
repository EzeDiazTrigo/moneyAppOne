package com.souckan.moneyappone.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("dollar")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dolarapi.com/v1/dolares/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("crypto")
    fun provideCryptoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideHoroscopeApiService(@Named("dollar") retrofit: Retrofit): DollarAPIService {
        return retrofit.create(DollarAPIService::class.java)
    }

    @Provides
    fun provideCryptoAPI(@Named("crypto") retrofit: Retrofit): CryptoAPIService {
        return retrofit.create(CryptoAPIService::class.java)
    }
}