package com.ben.tribunewsdemo.view.adapter


import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class UploadPhotoAdapter(private val photos: MutableList<Uri>): RecyclerView.Adapter<UploadPhotoAdapter.UploadPhotoViewHolder>() {

    override fun getItemCount(): Int = photos.size

    var onItemClickListener: ((Int) -> Unit)? = null
    var onItemLongClickListener: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: UploadPhotoViewHolder, position: Int) {
        val options = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(60))
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(holder.itemView.context).load(photos[position].toString()).apply(options).into(holder.uploadPhotoImageView)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(position)
            true
        }
    }

    fun onAddPhoto(position: Int, photo: Uri) {
        Log.d("Test", "$position")
        if(!this.photos.contains(photo)) {
            this.photos.add(position, photo)
            notifyDataSetChanged()
        }
    }

    fun onClearItems() {
        this.photos.clear()
        notifyDataSetChanged()
    }

    fun onRemoveItem(position: Int) {
        this.photos.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadPhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upload_photo_item, parent, false)
        return UploadPhotoViewHolder(view)
    }

    inner class UploadPhotoViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val uploadPhotoImageView: ImageView = v.findViewById(R.id.upload_photo_display)
    }
}