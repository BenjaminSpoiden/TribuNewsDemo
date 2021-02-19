package com.ben.tribunewsdemo.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File

const val TAG = "FileUtils"
private const val DEBUG = false

fun getPath(context: Context?, uri: Uri): String? {
    if (DEBUG) Log.d(FileUtils.TAG + " File -",
            "Authority: " + uri.authority +
                    ", Fragment: " + uri.fragment +
                    ", Port: " + uri.port +
                    ", Query: " + uri.query +
                    ", Scheme: " + uri.scheme +
                    ", Host: " + uri.host +
                    ", Segments: " + uri.pathSegments.toString()
    )
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // LocalStorageProvider
        if (FileUtils.isLocalStorageDocument(uri)) {
            // The path is the id
            return DocumentsContract.getDocumentId(uri)
        } else if (FileUtils.isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }

            // TODO handle non-primary volumes
        } else if (FileUtils.isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong())
            return FileUtils.getDataColumn(context, contentUri, null, null)
        } else if (FileUtils.isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                    split[1]
            )
            return FileUtils.getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        return if (FileUtils.isGooglePhotosUri(uri)) uri.lastPathSegment else FileUtils.getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isLocal(url: String?): Boolean {
    return url != null && !url.startsWith("http://") && !url.startsWith("https://")
}

fun getFiles(context: Context?, uri: List<Uri>?): MutableList<File>? {
    val pathNames = mutableListOf<File>()
    if (uri != null) {
        uri.forEach {
            val pathName = getPath(context, it)
            if(isLocal(pathName)) pathNames.add(File(pathName.toString()))
        }
        return pathNames
    }
    return null
}