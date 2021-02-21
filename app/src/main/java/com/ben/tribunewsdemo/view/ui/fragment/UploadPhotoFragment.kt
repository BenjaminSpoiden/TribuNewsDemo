package com.ben.tribunewsdemo.view.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
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
import com.ben.tribunewsdemo.view.adapter.UploadPhotoAdapter
import com.ben.tribunewsdemo.viewmodel.UploadPhotoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadPhotoFragment : Fragment(), CallbackListener<ResponseBody>, OnAddListener {

    private val uploadPhotoViewModel: UploadPhotoViewModel by activityViewModels()
    private lateinit var currentPhotoPath: String

    private lateinit var uploadPhotoRecyclerView: RecyclerView
    private lateinit var uploadPhotoAdapter: UploadPhotoAdapter
    private lateinit var addPhotosText: TextView
    private lateinit var sendButton: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUploadPhotoBinding.inflate(inflater, container, false)
        binding.uploadPhotoViewModel = uploadPhotoViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadPhotoViewModel.callbackListener = this
        uploadPhotoViewModel.onAddListener = this
        uploadPhotoRecyclerView = view.findViewById(R.id.upload_photo_rv)
        uploadPhotoAdapter = UploadPhotoAdapter(mutableListOf())
        addPhotosText = view.findViewById(R.id.add_photo_tv)

        uploadPhotoRecyclerView.apply {
            this.adapter?.setHasStableIds(true)
            this.adapter = uploadPhotoAdapter
        }

        sendButton = view.findViewById(R.id.send_button)

        uploadPhotoAdapter.onItemClickListener = {
            Toast.makeText(requireContext(), "Appuyez longement pour effacer une photo", Toast.LENGTH_SHORT).show()

        }

        uploadPhotoAdapter.onItemLongClickListener = {
            uploadPhotoViewModel.onRemoveItem(it)
            Log.d("Test", "Delete at pos $it")
            uploadPhotoAdapter.onRemoveItem(it)
            uploadButtonStateObserver()
        }

        addPhotoTextListener()
        onItemAdded()
    }

    private fun addPhotoTextListener() {
        addPhotosText.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (PackageManager.PERMISSION_DENIED) {
                    checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permission, PERMISSION_CODE)
                    }
                    checkSelfPermission(requireContext(), Manifest.permission.CAMERA) -> {
                        val permission = arrayOf(Manifest.permission.CAMERA)
                        requestPermissions(permission, PERMISSION_CODE)
                    }
                    else -> {
                        onMediaPick(it.context)
                    }
                }
            }else {
                onMediaPick(it.context)
            }
        }
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
    }


    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        Snackbar.make(requireView(), "Photos uploadées avec succès.", Snackbar.LENGTH_SHORT)
            .setAnchorView(requireActivity().findViewById(R.id.bottomNavigationView))
            .show()
        uploadPhotoViewModel.onClearItems()
        uploadPhotoAdapter.onClearItems()

        uploadButtonStateObserver()
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.d("Test", "onFailure: ${t.message}")
    }

    override fun onOverCapacity() {
        Toast.makeText(requireContext(), "Ne choisissez pas plus de 4 photos.", Toast.LENGTH_SHORT).show()
    }

    override fun onItemAdded() {
        uploadPhotoViewModel.filesUri.observe(viewLifecycleOwner, {
            it.forEachIndexed { position, uri ->
                uploadPhotoAdapter.onAddPhoto(position, uri)
            }
        })
        uploadButtonStateObserver()
    }

    private fun uploadButtonStateObserver() {
        uploadPhotoViewModel.isEnabled.observe(viewLifecycleOwner) {
            sendButton.isEnabled = it
        }
    }


    private fun onMediaPick(context: Context) {
        val options = arrayOf(
            PhotoMethodPicker.TAKE_PHOTO.method,
            PhotoMethodPicker.TAKE_GALLERY.method,
            PhotoMethodPicker.CANCEL.method
        )

        var intent: Intent

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Choisissez votre méthode")
        dialogBuilder.setItems(options) { dialog, which ->
            if(options[which] == PhotoMethodPicker.TAKE_PHOTO.method) {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicIntent ->
                    takePicIntent.also {
                        val photoFile: File? = try {
                            createImageFile()
                        }catch (ex: IOException) {
                            Log.d("Test", "Exception: $ex")
                            null
                        }
                        photoFile?.also { file ->
                            val photoURI = FileProvider.getUriForFile(
                                requireContext().applicationContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file
                            )

                            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        }
                    }
                }
                startActivityForResult(intent, IMAGE_CAPTURE)
            }
            if(options[which] == PhotoMethodPicker.TAKE_GALLERY.method) {
                intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Sélectionnez des photos"), IMAGE_CHOOSE)
            }
            if(options[which] == PhotoMethodPicker.CANCEL.method) {
                dialog.dismiss()
            }
        }
        dialogBuilder.show()
    }


    private fun onAddPicToPhoneGallery() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), arrayOf(file.name), null)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            onAddPicToPhoneGallery()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uploadPhotoViewModel.callbackListener = null
        uploadPhotoViewModel.onAddListener = null
    }

    companion object {
        private const val IMAGE_CHOOSE = 1000
        const val PERMISSION_CODE = 1001
        private const val IMAGE_CAPTURE = 2000
    }
}