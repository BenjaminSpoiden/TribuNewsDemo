package com.ben.tribunewsdemo.view.adapter.items

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.ben.tribunewsdemo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class UploadPhotoItem(val fileUri: Uri? = null, val context: Context): AbstractItem<UploadPhotoItem.UploadPhotoItemViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.upload_photo_item
    override val type: Int
        get() = R.id.upload_photo_rv

    override fun getViewHolder(v: View): UploadPhotoItemViewHolder {
        return UploadPhotoItemViewHolder(v)
    }

    inner class UploadPhotoItemViewHolder(v: View): FastAdapter.ViewHolder<UploadPhotoItem>(v) {

        private val displayedPhoto: ImageView = v.findViewById(R.id.upload_photo_display)

        override fun bindView(item: UploadPhotoItem, payloads: List<Any>) {
            Log.d("Test", "bindView with ${item.fileUri}")
            val options = RequestOptions()
                    .transform(CenterCrop(), RoundedCorners(60))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide
                    .with(item.context)
                    .load(item.fileUri.toString())
                    .apply(options)
                    .into(displayedPhoto)


        }

        override fun unbindView(item: UploadPhotoItem) {
            displayedPhoto.setImageURI(null)
        }
    }
}