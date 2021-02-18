package com.ben.tribunewsdemo.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.ben.tribunewsdemo.R
import com.ben.tribunewsdemo.api.ApiService
import com.ben.tribunewsdemo.api.RetrofitClientInstance
import com.ben.tribunewsdemo.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.io.File
import java.lang.Exception
import java.nio.Buffer
import java.nio.file.FileSystems


/**
 * A simple [Fragment] subclass.
 * Use the [UploadPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadPhotoFragment : Fragment() {

    private lateinit var uploadPhotoDisplay: ImageView
    private lateinit var uploadPhotoDisplay2: ImageView
    private lateinit var uploadPhotoDisplay3: ImageView
    private lateinit var uploadPhotoDisplay4: ImageView
    private lateinit var uploadPhotoGridLayout: GridLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadPhotoDisplay = view.findViewById(R.id.upload_photo_display)
        uploadPhotoDisplay2 = view.findViewById(R.id.upload_photo_display2)
        uploadPhotoDisplay3 = view.findViewById(R.id.upload_photo_display3)
        uploadPhotoDisplay4 = view.findViewById(R.id.upload_photo_display4)
        uploadPhotoGridLayout = view.findViewById(R.id.upload_photo_gridLayout)

        uploadPhotoDisplay.clipToOutline = true

        uploadPhotoGridLayout.setOnClickListener {
            Log.d("Test", "imageclick")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context?.let { it1 -> checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                   onPickImageGallery()
                }
            }else {
               onPickImageGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Test", "Data: ${data?.dataString}")
        uploadPhotoDisplay.setImageURI(data?.data)
        uploadFile(data?.data!!)
    }

    private fun uploadFile(fileUri: Uri) {
        val tribuNewsService = RetrofitClientInstance.retrofitInstance?.create(ApiService::class.java)

        val file = FileUtils.getFile(context, fileUri)

        Log.d("Test", "File: $file")

        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("image/*"),
            file
        )

        Log.d("Test", "Request File: $requestFile")

        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = tribuNewsService?.onUploadPicture(body) ?: return

        response.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Test", "Response: ${response.body()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Test", "Cause: ${t.cause}")
                Log.d("Test", "Message: ${t.message}")
            }
        })
    }

    private fun onPickImageGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(Intent.createChooser(galleryIntent, "Select photos"), IMAGE_CHOOSE)
    }

    private fun onCapturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE)
    }

    companion object {
        @JvmStatic
        fun newInstance() = UploadPhotoFragment()
        private val IMAGE_CHOOSE = 1000
        private val PERMISSION_CODE = 1001
        private val IMAGE_CAPTURE = 2000
    }
}