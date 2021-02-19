package com.ben.tribunewsdemo.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.ben.tribunewsdemo.R

class CustomImageAdapter(private val context: Activity, private val images: List<Uri>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.fragment_upload_photo, parent)

        val imageView = rowView.findViewById<ImageView>(R.id.upload_photo_display)
        imageView.setImageURI(images[position])

        return rowView
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}