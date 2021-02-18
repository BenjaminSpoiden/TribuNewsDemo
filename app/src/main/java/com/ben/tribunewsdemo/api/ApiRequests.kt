package com.ben.tribunewsdemo.api

import com.ben.tribunewsdemo.api.model.Images
import retrofit2.Call
import retrofit2.http.GET

interface ApiRequests {

    @GET("/pics")
    fun onGetAllPictures(): Call<Images>
}