package com.ben.tribunewsdemo.view.adapter.items

import android.view.View
import android.widget.ImageView
import com.ben.tribunewsdemo.R
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class UploadPhotoItem(val fileUri: String? = null): AbstractItem<UploadPhotoItem.UploadPhotoItemViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.upload_photo_item
    override val type: Int
        get() = R.id.upload_photo_rv

    override fun getViewHolder(v: View): UploadPhotoItemViewHolder {
        return UploadPhotoItemViewHolder(v)
    }


    override var isEnabled: Boolean
        get() = true
        set(value) {}

    inner class UploadPhotoItemViewHolder(v: View): FastAdapter.ViewHolder<UploadPhotoItem>(v) {

        private val displayedPhoto: ImageView = v.findViewById(R.id.upload_photo_display)

        override fun bindView(item: UploadPhotoItem, payloads: List<Any>) {
            Glide
            .with(displayedPhoto)
            .load(item.fileUri)
            .centerCrop()
            .placeholder(R.drawable.image_view_border)
            .into(displayedPhoto)
        }

        override fun unbindView(item: UploadPhotoItem) {
            displayedPhoto.setImageURI(null)
        }
    }
}