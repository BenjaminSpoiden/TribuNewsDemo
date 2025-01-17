package com.ben.tribunewsdemo.viewmodel

import androidx.lifecycle.*
import com.ben.tribunewsdemo.api.network.TribuNewsNetwork
import com.ben.tribunewsdemo.api.model.FileData
import kotlinx.coroutines.launch

class GalleryViewModel: ViewModel() {

    private val _galleryResponse = MutableLiveData<FileData>()
    val galleryResponse: LiveData<FileData>
        get() = _galleryResponse

    fun onFetchGallery() {
        viewModelScope.launch {
            _galleryResponse.value = TribuNewsNetwork.retrofit?.onGetAllPictures() ?: return@launch
        }
    }

}