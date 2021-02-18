package com.ben.tribunewsdemo.api

import com.ben.tribunewsdemo.api.model.Images
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.HashMap

interface ApiService {

    @GET("/pics")
    fun onGetAllPictures(): Call<Images>

    @Multipart()
    @POST("/pic-upload")
    suspend fun onUploadPicture(@PartMap() map: HashMap<String?, RequestBody?>): Response<ResponseBody>
}