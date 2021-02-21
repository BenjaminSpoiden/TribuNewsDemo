package com.ben.tribunewsdemo.view.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_ITEM
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_LOADING
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PhotoAdapter(private var files: MutableList<String>): RecyclerView.Adapter<BaseViewHolder<*>>() {

    inner class PhotoViewHolder(v: View): BaseViewHolder<String>(v) {
        private val photoImageView: ImageView = v.findViewById(R.id.photo_image_view)
        override fun onBind(holder: MutableList<String>, position: Int) {
            Glide
                .with(photoImageView)
                .load(holder[position])
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_baseline_photo_24)
                .into(photoImageView)
        }

    }
    inner class LoaderViewHolder(v: View): BaseViewHolder<Any>(v) {
        override fun onBind(holder: MutableList<String>, position: Int) {
            //...
        }
    }


    fun onAddPhoto(photo: String, position: Int) {
        this.files.add(photo)
        notifyItemInserted(position)
    }

    fun onAddPhotos(photos: List<String>) {
        this.files.addAll(photos)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        Handler(Looper.getMainLooper()).post {
            files.add("")
            files.size.minus(1).let { notifyItemInserted(it) }
        }
    }

    fun removeLoadingView() {
        if (files.size != 0) {
            files.size.minus(1).let { files.removeAt(it) }
            notifyItemRemoved(files.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return if(viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_rv_item, parent, false)
            PhotoViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
            LoaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if(holder.itemViewType == VIEW_TYPE_ITEM) {
            (holder as PhotoViewHolder).onBind(files, position)
        }else if(holder.itemViewType == VIEW_TYPE_LOADING) {
            (holder as LoaderViewHolder).onBind(files, position)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (files[position] == "") {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int  = files.size
}