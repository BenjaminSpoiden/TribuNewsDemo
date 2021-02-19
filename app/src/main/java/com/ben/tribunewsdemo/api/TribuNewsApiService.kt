package com.ben.tribunewsdemo.api

import com.ben.tribunewsdemo.api.model.FileData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TribuNewsApiService {

    @GET("/pics")
    suspend fun onGetAllPictures(): FileData

    @Multipart
    @POST("/pic-upload")
    fun onUploadPicture(@Part files: List<MultipartBody.Part>): Call<ResponseBody>
}