package com.ben.tribunewsdemo.view.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.interfaces.OnLoadMoreListener
import com.ben.tribunewsdemo.utils.Constants.LIMIT
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_ITEM
import com.ben.tribunewsdemo.utils.Constants.VIEW_TYPE_LOADING
import com.ben.tribunewsdemo.utils.InfiniteScrollHelper
import com.ben.tribunewsdemo.view.adapter.PhotoAdapter
import com.ben.tribunewsdemo.view.adapter.items.PhotoItem
import com.ben.tribunewsdemo.viewmodel.GalleryViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {

    private val galleryViewModel: GalleryViewModel by activityViewModels()

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var galleryProgressBar: ProgressBar
    private lateinit var galleryLoadingText: TextView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var infiniteScrollListener: InfiniteScrollHelper
//    private val itemAdapter = ItemAdapter<PhotoItem>()
//    private val fastAdapter = FastAdapter.with(itemAdapter)

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

        galleryViewModel.onFetchGallery()
        setInitialData()
        setAdapterAndLayout()
        setScrollListener()
    }

    private fun setInitialData() {
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

    private fun setScrollListener() {
        infiniteScrollListener = InfiniteScrollHelper(gridLayoutManager)
        infiniteScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onMoreLoaded()
            }
        })

        photoRecyclerView.addOnScrollListener(infiniteScrollListener)
    }

    private fun onMoreLoaded() {
        photoAdapter.addLoadingView()
        Log.d("Test", "Loaded more...")
        val start = photoAdapter.itemCount
        val offset = start + LIMIT

        Log.d("Test", "start: $start")
        Log.d("Test", "offset: $offset")

        Handler().postDelayed({
            galleryViewModel.galleryResponse.observe(viewLifecycleOwner) {
                for(i in start..offset) {
                    Log.d("Test", "i pos: $i")
                    Log.d("Test", "files: ${it.files.size}")
                    if(i < it.files.size - 1) {
                        Log.d("Test", "Can add")
                        photoAdapter.onAddPhoto(it.files[i], i)
                        photoAdapter.notifyItemInserted(i)
                    }
                    photoAdapter.removeLoadingView()
                    infiniteScrollListener.setLoaded()
                }
            }


            photoRecyclerView.post {
                photoAdapter.notifyDataSetChanged()
            }

        }, 1000)


    }

    companion object {
        @JvmStatic
        fun newInstance() = GalleryFragment()
    }
}