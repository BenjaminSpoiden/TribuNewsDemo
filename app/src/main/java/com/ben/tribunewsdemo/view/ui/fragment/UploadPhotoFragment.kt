package com.ben.tribunewsdemo.view.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ben.tribunewsdemo.BuildConfig
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.databinding.FragmentUploadPhotoBinding
import com.ben.tribunewsdemo.interfaces.CallbackListener
import com.ben.tribunewsdemo.interfaces.OnAddListener
import com.ben.tribunewsdemo.utils.PhotoMethodPicker
import com.ben.tribunewsdemo.view.adapter.items.UploadPhotoItem
import com.ben.tribunewsdemo.viewmodel.UploadPhotoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [UploadPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadPhotoFragment : Fragment(), CallbackListener<ResponseBody>, OnAddListener {

    private val uploadPhotoViewModel: UploadPhotoViewModel by activityViewModels()
    private lateinit var currentPhotoPath: String

    private lateinit var uploadPhotoRecyclerView: RecyclerView
    private lateinit var addPhotosText: TextView
    private lateinit var sendButton: MaterialButton

    private val itemAdapter = ItemAdapter<UploadPhotoItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUploadPhotoBinding.inflate(inflater, container, false)
        binding.uploadPhotoViewModel = uploadPhotoViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadPhotoViewModel.callbackListener = this
        uploadPhotoViewModel.onAddListener = this
        uploadPhotoRecyclerView = view.findViewById(R.id.upload_photo_rv)
        addPhotosText = view.findViewById(R.id.add_photo_tv)

        uploadPhotoRecyclerView.apply {
            this.adapter = fastAdapter
        }
        sendButton = view.findViewById(R.id.send_button)

        fastAdapter.onLongClickListener = { v, adapter, item, position ->
            Log.d("Test", "$position")
            uploadPhotoViewModel.onRemoveItem(position).observe(viewLifecycleOwner) {
                itemAdapter.remove(position)
                fastAdapter.notifyAdapterItemRemoved(position)
            }
            false
        }

        addPhotosText.setOnClickListener {
            Log.d("Test", "Clicked there")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context?.let { it1 -> checkSelfPermission(
                        it1,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) } == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                   onMediaPick(it.context)
                }
            }else {
               onMediaPick(it.context)
            }
        }

        observerSates()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_CANCELED) {
            when(requestCode) {
                IMAGE_CAPTURE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        uploadPhotoViewModel.onAddItem(Uri.fromFile(File(currentPhotoPath)))
                    }
                }
                IMAGE_CHOOSE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        if (data.clipData != null) {
                            for (i in 0 until data.clipData!!.itemCount) {
                                data.clipData?.getItemAt(i)?.uri?.let {
                                    uploadPhotoViewModel.onAddItem(it)
                                }
                            }
                        } else {
                            data.data?.let {
                                uploadPhotoViewModel.onAddItem(it)
                            }
                        }
                    }
                }
            }
        }

        observerSates()
    }


    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        Log.d("Test", "Response: ${response.raw().body()}")
        Snackbar.make(requireView(), "Photos successfully Loaded", 3000).show()
        uploadPhotoViewModel.onClearItems()
        itemAdapter.clear()
        fastAdapter.notifyAdapterDataSetChanged()
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.d("Test", "onFailure: ${t.message}")
    }

    override fun onOverCapacity() {
        Toast.makeText(requireContext(), "Please load only 4 items.", Toast.LENGTH_SHORT).show()
    }


    private fun onMediaPick(context: Context) {
        val options = arrayOf(
            PhotoMethodPicker.TAKE_PHOTO.method,
            PhotoMethodPicker.TAKE_GALLERY.method,
            PhotoMethodPicker.CANCEL.method
        )

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Choose your method")
        dialogBuilder.setItems(options) { dialog, which ->
            if(options[which] == PhotoMethodPicker.TAKE_PHOTO.method) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicIntent ->
                    takePicIntent.also {
                        val photoFile: File? = try {
                            createImageFile()
                        }catch (ex: IOException) {
                            Log.d("Test", "Exception: $ex")
                            null
                        }
                        Log.d("Test", "Photo File: $photoFile")
                        photoFile?.also { file ->
                            val photoURI = FileProvider.getUriForFile(
                                requireContext().applicationContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file
                            )
                            Log.d("Test", "Photo URI: $photoURI")

                            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            Log.d("Test", "Take Pic: ${takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)}")
                        }
                    }
                }
                startActivityForResult(intent, IMAGE_CAPTURE)
            }
            if(options[which] == PhotoMethodPicker.TAKE_GALLERY.method) {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Select Photos"), IMAGE_CHOOSE)
            }
            if(options[which] == PhotoMethodPicker.CANCEL.method) {
                dialog.dismiss()
            }
        }
        dialogBuilder.show()
    }

    private fun observerSates() {
        uploadPhotoViewModel.filesUri.observe(viewLifecycleOwner, {
            Log.d("Test", "We are in")
            itemAdapter.clear()
            it.forEach { uri ->
                itemAdapter.add(UploadPhotoItem(uri.toString()))
            }
            fastAdapter.notifyAdapterDataSetChanged()
        })

        uploadPhotoViewModel.isEnabled.observe(viewLifecycleOwner) {
            sendButton.isEnabled = it
        }
    }


    private fun onAddPicToGallery() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), arrayOf(file.name), null)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("Test", "Storage Dir: $storageDir")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            onAddPicToGallery()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = UploadPhotoFragment()
        private val IMAGE_CHOOSE = 1000
        private val PERMISSION_CODE = 1001
        private val IMAGE_CAPTURE = 2000
    }
}