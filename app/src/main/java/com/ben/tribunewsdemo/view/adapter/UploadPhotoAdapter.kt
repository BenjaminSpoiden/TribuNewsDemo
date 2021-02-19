package com.ben.tribunewsdemo.view.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R


class UploadPhotoAdapter(private val filesUris: List<Uri>?): RecyclerView.Adapter<UploadPhotoAdapter.UploadPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadPhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upload_photo_item, parent, false)
        return UploadPhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadPhotoViewHolder, position: Int) {
        Log.d("Test", "$filesUris")
        if(filesUris != null) {
            holder.image.setImageURI(filesUris[position])
        }
    }

    override fun getItemCount(): Int {
        return filesUris?.size ?: 0
    }

    inner class UploadPhotoViewHolder(v: View): RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.upload_photo_display)
    }
}