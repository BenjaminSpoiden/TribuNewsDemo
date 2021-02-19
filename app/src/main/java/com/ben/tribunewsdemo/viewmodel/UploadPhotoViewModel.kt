package com.ben.tribunewsdemo.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.ben.tribunewsdemo.api.network.TribuNewsNetwork
import com.ben.tribunewsdemo.interfaces.CallbackListener
import com.ben.tribunewsdemo.interfaces.OnAddListener
import com.ben.tribunewsdemo.utils.getFiles
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part


class UploadPhotoViewModel(application: Application): AndroidViewModel(application) {
    var callbackListener: CallbackListener<ResponseBody>? = null
    var onAddListener: OnAddListener? = null

    private val _filesUri: MutableLiveData<MutableList<Uri>> = MutableLiveData(mutableListOf())
    val filesUri: LiveData<MutableList<Uri>>
        get() = _filesUri


    private val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEnabled: LiveData<Boolean>
        get() {
            _isEnabled.value = filesUri.value?.isNotEmpty()
            return _isEnabled
        }

    private fun onUploadPhotos(
        @Part files: List<MultipartBody.Part>
    ) {
        viewModelScope.launch {
           TribuNewsNetwork.retrofit?.onUploadPictures(files)?.enqueue(object: Callback<ResponseBody> {
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   callbackListener?.onResponse(call, response)
               }

               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   callbackListener?.onFailure(call, t)
               }
           })
        }
    }

    fun onAddItem(fileUri: Uri) {
        if(filesUri.value?.size!! < 4) {
            _filesUri.value?.add(fileUri)
        } else {
            onAddListener?.onOverCapacity()
        }
    }

    fun onUpload() {
        val files = getFiles(getApplication(), filesUri.value)
        val parts: ArrayList<MultipartBody.Part> = ArrayList()
        files?.forEach { file ->
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

            parts.add(MultipartBody.Part.createFormData("file", file.name, requestFile))
        }
        onUploadPhotos(parts)
    }

    fun onClearItems() {
        _filesUri.value?.clear()
    }
}