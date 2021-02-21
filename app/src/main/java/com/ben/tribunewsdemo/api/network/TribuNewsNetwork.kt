package com.ben.tribunewsdemo.api.network

import com.ben.tribunewsdemo.api.TribuNewsApiService
import com.ben.tribunewsdemo.utils.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TribuNewsNetwork {
    val retrofit: TribuNewsApiService? by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TribuNewsApiService::class.java)
    }
}