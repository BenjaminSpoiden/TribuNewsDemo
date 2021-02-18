package com.ben.tribunewsdemo.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiRequests {

    @GET("/pics")
    fun getAllPictures(): Call<Images>
}