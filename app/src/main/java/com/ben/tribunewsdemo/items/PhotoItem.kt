package com.ben.tribunewsdemo.items

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ben.tribunewsdemo.R
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class PhotoItem(val fileUrl: String? = null): AbstractItem<PhotoItem.PhotoViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.photo_rv_item

    override val type: Int
        get() = R.id.photos_rv

    override fun getViewHolder(v: View): PhotoViewHolder {
        return PhotoViewHolder(v)
    }

    inner class PhotoViewHolder(v: View): FastAdapter.ViewHolder<PhotoItem>(v) {
        private val photo: ImageView = v.findViewById(R.id.photo_image_view)

        override fun bindView(item: PhotoItem, payloads: List<Any>) {

            Glide
                .with(photo)
                .load(item.fileUrl)
                .centerCrop()
                .into(photo)
        }

        override fun unbindView(item: PhotoItem) {
            photo.setImageURI(null)
        }
    }
}