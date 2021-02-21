package com.ben.tribunewsdemo.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(v: View): RecyclerView.ViewHolder(v) {
    abstract fun onBind(holder: MutableList<String>, position: Int)
}