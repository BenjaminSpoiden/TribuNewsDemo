package com.ben.tribunewsdemo.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.ben.tribunewsdemo.api.network.TribuNewsNetwork
import com.ben.tribunewsdemo.interfaces.CallbackListener
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
import java.util.*

class UploadPhotoViewModel(application: Application): AndroidViewModel(application) {
    var callbackListener: CallbackListener<ResponseBody>? = null

    var isEnabledButton = false

    private val _filesUri: MutableLiveData<MutableList<Uri>> = MutableLiveData(mutableListOf())
    val fileUri: LiveData<List<Uri>>
        get() = _filesUri as LiveData<List<Uri>>


    private val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEnabled: LiveData<Boolean>
        get() {
            _isEnabled.value = fileUri.value?.size!! > 0
            return _isEnabled
        }

    private fun onUploadPhoto(@Part file: MultipartBody.Part) {
        viewModelScope.launch {
           TribuNewsNetwork.retrofit?.onUploadPicture(file)?.enqueue(object: Callback<ResponseBody> {
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
        Log.d("Test", "onAddItem")
        _filesUri.value?.add(fileUri)
    }

    fun onUpload() {
        Log.d("Test", "onUpload: ${_filesUri.value?.size}")
        val files = getFiles(getApplication(), fileUri.value)
        Log.d("Test", "Files: $files")
        files?.forEach { file ->
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            onUploadPhoto(body)
        }
    }

    fun onClearItems() {
        _filesUri.value?.clear()
    }
}