package com.ben.tribunewsdemo.utils

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.interfaces.OnLoadMoreListener

class InfiniteScrollHelper(layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {


    private var visibleThreshold = 5
    private lateinit var onLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var gridLayoutManager = layoutManager

    fun cancelLoading() {
        isLoading = false
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    init {
        visibleThreshold *= layoutManager.spanCount
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) return

        totalItemCount = gridLayoutManager.itemCount

        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()

        if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            onLoadMoreListener.onLoadMore()
            isLoading = true
        }
    }
}