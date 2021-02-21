package com.ben.tribunewsdemo.view.adapter

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_ITEM
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_LOADING
import com.bumptech.glide.Glide

class PhotoAdapter(private var files: MutableList<String>): RecyclerView.Adapter<BaseViewHolder<*>>() {

    inner class PhotoViewHolder(v: View): BaseViewHolder<String>(v) {
        private val photoImageView: ImageView = v.findViewById(R.id.photo_image_view)
        override fun onBind(holder: MutableList<String>, position: Int) {
            Glide
                .with(photoImageView)
                .load(holder[position].toString())
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_photo_24)
                .into(photoImageView)
        }

    }
    inner class LoaderViewHolder(v: View): BaseViewHolder<Any>(v) {
        private  val progressBarFooter: ProgressBar = v.findViewById(R.id.progressBar_footer)
        override fun onBind(holder: MutableList<String>, position: Int) {

        }
    }

    fun getItem(position: Int) = files[position]

    fun onAddPhoto(photo: String, position: Int) {
//        Log.d("Test", "Photo: $photo")
        this.files.add(photo)
        notifyItemInserted(position)
    }

    fun onAddPhotos(photos: List<String>) {
        this.files.addAll(photos)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //Add loading item
        Handler().post {
            files.add("")
            files.size.minus(1).let { notifyItemInserted(it) }
        }
    }

    fun removeLoadingView() {
        //Remove loading item
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