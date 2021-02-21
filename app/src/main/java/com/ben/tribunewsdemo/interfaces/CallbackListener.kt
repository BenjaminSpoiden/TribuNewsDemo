package com.ben.tribunewsdemo.interfaces

import retrofit2.Call
import retrofit2.Response

interface CallbackListener<T> {
    fun onResponse(call: Call<T>, response: Response<T>)
    fun onFailure(call: Call<T>, t: Throwable)
}