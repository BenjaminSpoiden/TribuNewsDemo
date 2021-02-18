package com.ben.tribunewsdemo.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.api.ApiService
import com.ben.tribunewsdemo.api.RetrofitClientInstance
import com.ben.tribunewsdemo.items.PhotoItem
import com.ben.tribunewsdemo.utils.BASE_URL
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {

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

        val tribuNewsService = RetrofitClientInstance.retrofitInstance?.create(ApiService::class.java)

        //Input Output dispatcher for managing data
        GlobalScope.launch(Dispatchers.IO) {

            val response = tribuNewsService?.onGetAllPictures()?.awaitResponse() ?: return@launch
            if(response.isSuccessful) {
                val data = response.body()!!
                Log.d("test", "${data.files.size}")

                //Dispatchers.Main to make changes on the UI
                withContext(Dispatchers.Main) {
                    galleryProgressBar.visibility = View.GONE
                    galleryLoadingText.visibility = View.GONE
                    data.files.forEach {
                        itemAdapter.add(PhotoItem(it))
                    }
                    fastAdapter.notifyAdapterDataSetChanged()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GalleryFragment()
    }
}