package com.ben.tribunewsdemo.view.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.interfaces.OnLoadMoreListener
import com.ben.tribunewsdemo.utils.Constants.LIMIT
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_ITEM
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_LOADING
import com.ben.tribunewsdemo.utils.InfiniteScrollHelper
import com.ben.tribunewsdemo.view.adapter.PhotoAdapter
import com.ben.tribunewsdemo.viewmodel.GalleryViewModel

class GalleryFragment : Fragment() {

    private val galleryViewModel: GalleryViewModel by viewModels()
    private var recyclerViewState: Parcelable? = null
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var galleryProgressBar: ProgressBar
    private lateinit var galleryLoadingText: TextView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private var infiniteScrollListener: InfiniteScrollHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        photoRecyclerView = view.findViewById(R.id.photos_rv)
        galleryProgressBar = view.findViewById(R.id.gallery_progress_bar)
        galleryLoadingText = view.findViewById(R.id.gallery_loading_text)

        setAdapterAndLayout()
        setInitialData()
        setScrollListener()
    }

    private fun setInitialData() {
        galleryViewModel.onFetchGallery()
        galleryViewModel.galleryResponse.observe(viewLifecycleOwner, {
            galleryProgressBar.visibility = View.GONE
            galleryLoadingText.visibility = View.GONE

            photoAdapter.onAddPhotos(it.files.take(LIMIT))

        })
    }



    private fun setAdapterAndLayout() {
        photoAdapter = PhotoAdapter(mutableListOf())
        gridLayoutManager = GridLayoutManager(requireContext(), 3)
        photoRecyclerView.apply {
            this.layoutManager = gridLayoutManager
            this.setHasFixedSize(true)
            this.adapter = photoAdapter
            this.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (photoAdapter.getItemViewType(position)) {
                    VIEW_TYPE_ITEM -> 1
                    VIEW_TYPE_LOADING -> 3
                    else -> -1
                }
            }
        }
    }


    override fun onDestroyView() {
        infiniteScrollListener = null
        recyclerViewState = photoRecyclerView.layoutManager?.onSaveInstanceState()
        super.onDestroyView()

    }

    private fun setScrollListener() {
        infiniteScrollListener = InfiniteScrollHelper(gridLayoutManager)
        infiniteScrollListener?.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onMoreLoaded()
            }
        })

        infiniteScrollListener?.let {
            photoRecyclerView.addOnScrollListener(it)
        }
    }


    private fun onMoreLoaded() {
        photoAdapter.addLoadingView()
        val start = photoAdapter.itemCount
        val offset = start + LIMIT -1


        Handler(Looper.getMainLooper()).postDelayed({
            galleryViewModel.galleryResponse.observe(viewLifecycleOwner) {
                photoAdapter.removeLoadingView()
                for(i in start..offset) {
                    if(i < it.files.size - 1) {
                        photoAdapter.onAddPhoto(it.files[i], i)
                    }
                }
                infiniteScrollListener?.cancelLoading()
            }

        }, 1000)
    }
}