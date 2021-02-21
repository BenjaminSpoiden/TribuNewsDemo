package com.ben.tribunewsdemo.view.ui.fragment

import android.os.Bundle
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

    private val itemAdapter = ItemAdapter<PhotoItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

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

        photoRecyclerView.apply {
            this.adapter = fastAdapter
            this.layoutManager = GridLayoutManager(context, 3)
        }

        galleryViewModel.onFetchGallery()
        galleryViewModel.galleryResponse.observe(viewLifecycleOwner, {
            galleryProgressBar.visibility = View.GONE
            galleryLoadingText.visibility = View.GONE

            it.files.forEach { file ->
                itemAdapter.add(PhotoItem(file))
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = GalleryFragment()
    }
}